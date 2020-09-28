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

  def ls(path: String): Action[AnyContent] = authUtils.async(parse.defaultBodyParser) { req =>
    req.attrs.get(Attrs.Context) match {
      case Some(context) => {
        fileDAO.ls(path, context.id).map { fileList: List[DBFile] =>
          Ok(Json.toJson(fileList))
        }.recover {
          case ex: Exception => InternalServerError(ex.getMessage)
        }
      }
      case _             => Future successful InternalServerError("No context")
    }
  }

  def upload: Action[MultipartFormData[Files.TemporaryFile]] = authUtils.async(parse.multipartFormData) { req =>
    req.attrs.get(Attrs.Context) match {
      case Some(context) => {
        req.body.files.map(file => {
          fileDAO.add(file, context.id)
        })
        Future successful Ok("Uploaded")
      }
      case None          => Future successful InternalServerError("No context")
    }
  }

  def delete = authUtils.async(parse.defaultBodyParser) { req =>
    req.attrs.get(Attrs.Context) match {
      case Some(context) => {
        //! Here goes the delete call
        Future successful Ok("")
      }
      case None          => Future successful InternalServerError("No context")
    }
  }

}