package utils

import com.typesafe.config.ConfigFactory
import javax.inject.Inject
import models.{Attrs, Context, DBUser}
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import play.api.libs.json.{JsSuccess, Json, Writes}
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


case class Auth @Inject()(override val parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser) {

  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
    request.headers.toMap.get("Authorization") match {
      case Some(List(bearerToken)) if bearerToken.split(' ').toList.nonEmpty => {
        Jwt.decode(bearerToken.split(' ').toList(1), ConfigFactory.load().getString("jwt.secret"), Seq(JwtAlgorithm.HS256)) match {
          case Success(tokenPayload)  => {
            Json.parse(tokenPayload).validate[Context](Context.read) match {
              case JsSuccess(context, _) => block(request.addAttr(Attrs.Context, context))
              case _                     => Future.successful(InternalServerError("No context could be set"))
            }
          }
          case Failure(ex: Throwable) => Future.successful(Unauthorized(ex.getMessage()))
        }
      }
      case _                                                                 =>
        Future.successful(Unauthorized("No bearer auth"))
    }
  }
}

object Auth {
  implicit val loginWrite: Writes[(String, String, String)] = (o: (String, String, String)) => {
    Json.obj(
      "email" -> o._1,
      "userName" -> o._2,
      "token" -> o._3,
    )
  }

  def createJWT(user: DBUser): String = {
    //! Todo make a writer : user
    Jwt.encode(JwtClaim({
      s"""{"name": "${user.name}", "email": "${user._email}", "id": "${user._id}"}"""
    }).issuedNow.expiresIn(86400 * 30), ConfigFactory.load().getString("jwt.secret"), JwtAlgorithm.HS256)
  }

  def verifyUserCrendentials(user: DBUser, userCredentials: (String, String)): Boolean = {
    if (hashPassword(userCredentials._2) == user.password) true else false
  }

  def hashPassword(text: String): String = {
    String.format("%064x", new java.math.BigInteger(1, java.security.MessageDigest.getInstance("SHA-256").digest(text.getBytes("UTF-8"))))
  }
}
