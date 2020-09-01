package models

import java.util.UUID

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.typedmap.TypedKey

case class Context(
  exp: Int,
  iat: Int,
  name: String,
  email: String,
  id: String
)

object Context {
  val read: Reads[Context] = (
    (__ \ "exp").read[Int] and
      (__ \ "iat").read[Int] and
      (__ \ "name").read[String] and
      (__ \ "email").read[String] and
      (__ \ "id").read[String]
    ) (Context.apply _)
}

object Attrs {
  val Context: TypedKey[Context] = TypedKey.apply[Context]("Context")
}