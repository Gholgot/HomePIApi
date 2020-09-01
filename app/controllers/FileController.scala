package controllers

import akka.stream.Materializer
import dao.FileDAO
import javax.inject.{Inject, Singleton}
import models.{Attrs, DBFile}
import play.api.mvc.{AbstractController, ControllerComponents}
import play.libs.Json
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

  def ls(path: String) = authUtils.async(parse.defaultBodyParser) { req =>
    req.attrs.get(Attrs.Context) match {
      case Some(context) => {
        fileDAO.ls(path, context.id).map { fileList: List[DBFile] =>
          fileList.map { file: DBFile =>
            val json = DBFile.fileWrite.writes(file)
            println(json)
          }
        }
        Future successful Ok("Done")
      }
      case _             => Future successful InternalServerError("No context")
    }
  }

  def upload = authUtils.async(parse.multipartFormData) { req =>
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
}