package dbModels

import java.time.Instant
import java.util.UUID

import cn.playscala.mongo.annotations.Entity
import javax.inject.Singleton
import models.File
import play.api.libs.json.{JsObject, Json, Writes}

@Singleton
@Entity("file")
case class DBFile(_id: String,
  name: String,
  size: Float,
  contentType: String,
  creationDate: Instant,
  lastUpdate: Instant,
  authorId: String,
)

object DBFile {
  implicit val fileWrite: Writes[DBFile] = (file: DBFile) => Json.obj(
    "name" -> file.name,
    "size" -> file.size,
    "contentType" -> file.contentType,
    "creationDate" -> file.creationDate.toString,
    "lastUpdate" -> file.lastUpdate.toString
  )

  def lsFilterJson(path: String, userId: String): JsObject = {
    Json.obj(
      "name" -> path,
      "authorId" -> userId
    )
  }

  def apply(file: File, authorId: String): DBFile = {
    DBFile(UUID.randomUUID().toString, file.name, file.size, file.contentType, Instant.now(), Instant.now(), authorId)
  }
}