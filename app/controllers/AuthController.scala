package controllers

import akka.stream.Materializer
import com.typesafe.config.ConfigFactory
import dao.UserDAO
import javax.inject.{Inject, Singleton}
import dbModels.DBUser.{userCreationRead, userRead}
import dbModels.DBUser
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.libs.json._
import utils.Auth
import utils.Auth._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class AuthController @Inject()(cc: ControllerComponents,
  authUtils: Auth,
  userDao: UserDAO,
  implicit val materializer: Materializer)
  extends AbstractController(cc) {

  def authentificate = Action.async(parse.json) { request =>
    request.body.asOpt(userRead) match {
      case Some(userInfos: (String, String)) =>
        userDao.getUserByEmail(userInfos._1).map {
          case Some(user: DBUser) => if (verifyUserCrendentials(user, userInfos)) Ok(Json.toJson(createJWT(user))) else BadRequest("Wrong password")
          case None               => BadRequest("Wrong creadentials")
        }
      case None                              => Future successful BadRequest("Expecting application/json request body")
    }

  }

  def ping = authUtils(parse.defaultBodyParser) { implicit request =>
    Ok("Pong !")
  }

  def createUserAsAdmin = Action.async(parse.json) { request =>
    request.body.asOpt(userCreationRead) match {
      case Some(formInfos: Tuple4[String, String, String, String]) =>
        if (formInfos._1 == ConfigFactory.load().getString("admin.password"))
          userDao.createUser(formInfos._2, formInfos._3, hashPassword(formInfos._4)) match {
            case result: Future[String] => result.map { string => Ok(string) }
            case _                      => Future successful InternalServerError("Internal Error")
          }
        else
          Future successful BadRequest("Wrong admin password")
      case None                                                    => Future successful BadRequest("Please verify the payload")
    }
  }
}