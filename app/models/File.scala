package models

import java.io.InputStream

import play.api.libs.Files
import play.api.mvc.MultipartFormData

case class File(
  name: String,
  contentType: String,
  inputStream: Option[InputStream],
  size: Long,
  absolutePath: Option[String],
  userId: String
) {

  def getBucketPath(): String = {
    s"${userId}/${name}"
  }
}

object File {
  def apply(formFile: MultipartFormData.FilePart[Files.TemporaryFile], userId: String): File = {
    File(formFile.filename, formFile.contentType.getOrElse(""), inputStream = None, formFile.fileSize, Some(formFile.ref.getAbsolutePath), userId)
  }

  def destroy(): Unit = {

  }
}
