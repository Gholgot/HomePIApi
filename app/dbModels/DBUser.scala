package dbModels

import java.time.Instant

import cn.playscala.mongo.annotations.Entity
import javax.inject.Singleton
import play.api.libs.functional.syntax._
import play.api.libs.json.{Json, Reads, Writes, __}


@Singleton
@Entity("user")
case class DBUser(_id: String,
  _email: String,
  name: String,
  password: String,
  creationDate: Instant,
  lastUpdateDate: Instant)

object DBUser {
  val userRead: Reads[(String, String)] = (
    (__ \ "email").read[String] and
      (__ \ "password").read[String]
    ).tupled


  val userWrite: Writes[(String, String)] = Writes { tuple2 =>
    Json.obj(
      "email" -> tuple2._1,
      "token" -> tuple2._2
    )
  }

  val userCreationRead: Reads[(String, String, String, String)] = (
    (__ \ "adminPassword").read[String] and
      (__ \ "email").read[String] and
      (__ \ "userName").read[String] and
      (__ \ "userPassword").read[String]
    ).tupled
}