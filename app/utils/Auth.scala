package utils

import java.time.Clock
import com.typesafe.config.ConfigFactory
import javax.inject.Inject
import models.{Attrs, Context}
import dbModels.DBUser
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

case class Auth @Inject()(override val parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser) {

  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
    request.headers.toMap.get("Authorization") match {
      case Some(List(bearerToken)) if bearerToken.split(' ').toList.nonEmpty => {
        Jwt.decode(bearerToken.split(' ')(1), ConfigFactory.load().getString("jwt.secret"), Seq(JwtAlgorithm.HS256)) match {
          case Success(tokenPayload)  => {
            Json.parse(tokenPayload.content).validate[Context](Context.read) match {
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
  implicit val clock: Clock = Clock.systemUTC

  implicit val loginWrite: Writes[(String, String, String)] = (o: (String, String, String)) => {
    Json.obj(
      "email" -> o._1,
      "userName" -> o._2,
      "token" -> o._3,
    )
  }

   implicit val tokenPayloadWrite: Writes[(String, String, String)] = (
    (__ \ "name").write[String] and
    (__ \ "email").write[String] and
    (__ \ "id").write[String]
  ).tupled

  def createJWT(user: DBUser): String = {
    //Todo make a writer : TokenWriter
    Jwt.encode(JwtClaim({s"""{"name": "${user.name}", "email": "${user._email}", "id": "${user._id}"}"""})
    .issuedNow.expiresIn(86400 * 30), ConfigFactory.load().getString("jwt.secret"), JwtAlgorithm.HS256)
  }

  def verifyUserCrendentials(user: DBUser, userCredentials: (String, String)): Boolean = {
    if (hashPassword(userCredentials._2) == user.password) true else false
  }

  def hashPassword(text: String): String = {
    String.format("%064x", new java.math.BigInteger(1, java.security.MessageDigest.getInstance("SHA-256").digest(text.getBytes("UTF-8"))))
  }
}
