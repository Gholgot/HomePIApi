package utils

import akka.stream.Materializer
import com.typesafe.config.ConfigFactory
import models.User
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import play.api.libs.json.{Json, Writes}
import play.api.libs.streams.Accumulator
import play.api.libs.typedmap.TypedKey
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}


object AuthUtils {

  implicit val loginWrite: Writes[(String, String, String)] = (o: (String, String, String)) => {
    Json.obj(
      "email" -> o._1,
      "userName" -> o._2,
      "token" -> o._3,
    )
  }

  object verifyJWT {
    def async(block: Request[AnyContent] => Future[Result])(implicit materializer: Materializer): EssentialAction = EssentialAction { reqHeaders =>

      //Jwt.decodeAll()

      DefaultActionBuilder.apply(PlayBodyParsers.apply().anyContent).async(request => block(request)).apply(reqHeaders)

    }

    def apply(block: Request[AnyContent] => Result)(implicit materializer: Materializer): EssentialAction = EssentialAction { reqHeaders =>
      reqHeaders.headers.toMap.get("Authorization") match {
        case Some(List(bearerToken)) if bearerToken.split(' ').toList.nonEmpty => {
          Jwt.decode(bearerToken.split(' ').toList(1), ConfigFactory.load().getString("jwt.secret"), Seq(JwtAlgorithm.HS256)) match {
            case Success(value)         => DefaultActionBuilder.apply(PlayBodyParsers.apply().anyContent).apply(request => block(request)).apply(reqHeaders)
            case Failure(ex: Throwable) => Accumulator.done(Unauthorized(ex.getMessage()))
          }
        }
        case _                                                                 => {
          Accumulator.done(Unauthorized("No bearer auth"))
        }
      }
    }
  }


  def createJWT(user: User): String = {
    //! Todo make a writer : user
    Jwt.encode(JwtClaim({
      s"""{"name": ${user.name}, "email": ${user._email}, "id": ${user._id}"""
    }).issuedNow.expiresIn(86400 * 30), ConfigFactory.load().getString("jwt.secret"), JwtAlgorithm.HS256)
  }

  def verifyUserCrendentials(user: User, userCredentials: (String, String)): Boolean = {
    if (hashPassword(userCredentials._2) == user.password) true else false
  }

  def hashPassword(text: String): String = {
    String.format("%064x", new java.math.BigInteger(1, java.security.MessageDigest.getInstance("SHA-256").digest(text.getBytes("UTF-8"))))
  }
}
