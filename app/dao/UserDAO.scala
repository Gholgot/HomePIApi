package dao

import java.time.Instant
import java.util.UUID

import javax.inject.Inject
import managers.DBManager
import dbModels.DBUser
import play.api.libs.json.{JsObject, JsString}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserDAO @Inject()(dbManager: DBManager) {
  def getUserByEmail(email: String): Future[Option[DBUser]] = {
    dbManager.findFirst[DBUser](JsObject(Seq("_email" -> JsString(email))))
  }

  def createUser(email: String, userName: String, password: String): Future[String] = {
    try {
      dbManager.insert[DBUser](DBUser(UUID.randomUUID().toString, email, userName, password, Instant.now(), Instant.now())).map { _ =>
        s"${userName} has been created"
      }
    } catch {
      case e: Throwable => throw e
    }

  }
}