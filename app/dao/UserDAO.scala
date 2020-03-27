package dao

import java.time.Instant
import java.util.UUID

import cn.playscala.mongo.Mongo
import javax.inject.Inject
import models.User
import scala.concurrent.ExecutionContext.Implicits.global


import scala.concurrent.Future

class UserDAO @Inject()(mongo: Mongo) {
  def getUserByEmail(email: String): Future[Option[User]] = {
    mongo.find[User]().first
  }

  def createUser(email: String, userName: String, password: String): Future[String] = {
    try {
      mongo.insertOne[User](User(UUID.randomUUID(), email, userName, password, Instant.now(), Instant.now())).map { _ =>
        s"${userName} has been created"
      }
    } catch {
      case e: Throwable => throw e
    }

  }
}