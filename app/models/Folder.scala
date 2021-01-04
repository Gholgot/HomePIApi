package models

import play.api.libs.json.{Reads, Writes, __}
import play.api.libs.functional.syntax._

case class Folder (
  id: String,
  name: String,
  parentId: Option[String] = None,
  ownerId: String
)

object Folder {
  implicit val reader: Reads[Folder] = (
    (__ \ "id").read[String] and
    (__ \ "name").read[String] and
    (__ \ "parentId").readNullable[String] and
    (__ \ "ownerId").read[String]
  )(Folder.apply _)

   implicit val writer: Writes[Folder] = (
    (__ \ "id").write[String] and
    (__ \ "name").write[String] and
    (__ \ "parentId").writeNullable[String] and
    (__ \ "ownerId").write[String]
  )(unlift(Folder.unapply _))
}