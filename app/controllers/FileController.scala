package controllers

import cn.playscala.mongo.Mongo
import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.AuthUtils.verifyJWT

import scala.concurrent.Future

@Singleton
class FileController @Inject()(cc: ControllerComponents,
                               mongo: Mongo)
  extends AbstractController(cc) {

//  def getFile = verifyJWT {
//  }
}