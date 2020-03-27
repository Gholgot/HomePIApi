package controllers

import akka.stream.Materializer
import com.typesafe.config.ConfigFactory
import dao.UserDAO
import javax.inject.{Inject, Singleton}
import models.User
import models.User.{userCreationRead, userRead}
import play.api.libs.streams.Accumulator
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import utils.AuthUtils
import utils.AuthUtils.{createJWT, hashPassword, verifyUserCrendentials}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class AuthController @Inject()(cc: ControllerComponents,
                               userDao: UserDAO,
                               implicit val materializer: Materializer)
  extends AbstractController(cc) {

  def login = Action.async { request: Request[AnyContent] =>
    val body: AnyContent = request.body
    body.asJson flatMap { json =>
      json.asOpt(userRead)
    } match {
      case Some(userInfos: (String, String)) =>
        userDao.getUserByEmail(userInfos._1).map {
          case Some(user: User) => if (verifyUserCrendentials(user, userInfos)) Ok(createJWT(user)) else BadRequest("Wrong password")
          case None             => BadRequest("Wrong creadentials")
        }
      case None                              => Future successful BadRequest("Expecting application/json request body")
    }

  }

  def ping = AuthUtils.verifyJWT { implicit request =>
    Ok("Pong !")
  }

  def createUserAsAdmin = Action.async { implicit request =>
    request.body.asJson flatMap { json =>
      json.asOpt(userCreationRead)
    } match {
      case Some(formInfos) =>
        if (formInfos._1 == ConfigFactory.load().getString("admin.password"))
          userDao.createUser(formInfos._2, formInfos._3, hashPassword(formInfos._4)) match {
            case result: Future[String] => result.map { string => Ok(string) }
            case err: Throwable         => Future successful InternalServerError(err.getMessage())
          }
        else
          Future successful BadRequest("Wrong admin password")
      case None            => Future successful BadRequest("Please verify the payload")
    }
  }
}