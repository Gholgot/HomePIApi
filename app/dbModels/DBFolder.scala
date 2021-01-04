package dbModels

import java.time.Instant
import java.util.UUID

import cn.playscala.mongo.annotations.Entity
import javax.inject.Singleton
import play.api.libs.json.{JsObject, Json, Writes}

case class DBFolder(
  id: String,
  name: String,
  parentId: Option[String],
  ownerId: String
)

object DBFolder {

}