package controllers

import akka.stream.Materializer
import dao.FileDAO
import dbModels.DBFile
import javax.inject.{Inject, Singleton}
import models.Attrs
import play.api.libs.Files
import play.api.libs.json.{Json, Writes}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, MultipartFormData}
import utils.Auth

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class FileController @Inject()(cc: ControllerComponents,
  authUtils: Auth,
  fileDAO: FileDAO,
  implicit val materializer: Materializer
)
  extends AbstractController(cc) {

  def ls(folderId: Option[String]): Action[AnyContent] = authUtils.async(parse.defaultBodyParser) { req =>
    req.attrs.get(Attrs.Context) match {
      case Some(context) => {
        fileDAO.ls(folderId, context.id).map { fileList: List[DBFile] =>
          Ok(Json.toJson(fileList))
        }
      }
      case _             => Future successful InternalServerError("No context")
    }
  }

  def upload(folderId: Option[String]): Action[MultipartFormData[Files.TemporaryFile]] = authUtils.async(parse.multipartFormData) { req =>
    req.attrs.get(Attrs.Context) match {
      case Some(context) => {
        req.body.files.map(file => {
          fileDAO.add(file, context.id, folderId)
        })
        Future successful Ok("Uploaded")
      }
      case None          => Future successful InternalServerError("No context")
    }
  }

  def delete(fileId: String, folderId: Option[String]) = authUtils.async(parse.defaultBodyParser) { req =>
    req.attrs.get(Attrs.Context) match {
      case Some(context) => {
        fileDAO.remove(fileId, context.id, folderId)
        Future successful Ok(fileId + " has been deleted")
      }
      case None          => Future successful InternalServerError("No context")
    }
  }

}