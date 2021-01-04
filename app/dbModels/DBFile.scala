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
  folderId: String,
)

object DBFile {
  val blobStringSeparator: String = "~~"

  implicit val fileWrite: Writes[DBFile] = (file: DBFile) => Json.obj(
    "id" -> file._id,
    "name" -> file.name,
    "size" -> file.size,
    "contentType" -> file.contentType,
    "creationDate" -> file.creationDate.toString,
    "lastUpdate" -> file.lastUpdate.toString,
    "folderId" -> file.folderId
  )

  def lsFilterJson(folderId: String, userId: String): JsObject = {
    Json.obj(
      "folderId" -> folderId,
      "authorId" -> userId
    )
  }

  def findByIdFilterJson(id: String): JsObject = {
    Json.obj(
      "_id" -> id
    )
  }

  def findByBlobStringFilterJson(id: String, folderId: String, authorId: String): JsObject = {
    Json.obj(
      "_id" -> id,
      "folderId" -> folderId,
      "authorId" -> authorId
    )
  }

  def getBlobString(dbFile: DBFile): String = {
    s"${dbFile.folderId}${blobStringSeparator}${dbFile.name}"
  }

  def parseBlobString(blobString: String): Tuple2[String, String] = {
    blobString.split(blobStringSeparator) match {
      case Array(folderId, fileName) => (folderId, fileName)
    }
  }

  def apply(file: File, authorId: String): DBFile = {
    DBFile(UUID.randomUUID().toString, file.name, file.size, file.contentType, Instant.now(), Instant.now(), authorId, file.folderId)
  }
}