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
  userId: String,
  folderId: String
) {

  def getBucketPath(): String = {
    s"${userId}/${folderId}~~${name}"
  }
}

object File {
  val defaultFolderId: String = "root"

  def apply(formFile: MultipartFormData.FilePart[Files.TemporaryFile], userId: String, maybeFolderId: Option[String]): File = {
    val folderId: String = maybeFolderId match {
      case Some(folderIdString) => folderIdString
      case None                 => defaultFolderId
    }

    File(formFile.filename, formFile.contentType.getOrElse(""), inputStream = None, formFile.fileSize, Some(formFile.ref.getAbsolutePath), userId, folderId)
  }

  def destroy(): Unit = {

  }

  def buildBucketPath(userId: String, folderId: String, fileName: String): String = {
    s"${userId}/${folderId}~~${fileName}"
  }
}
