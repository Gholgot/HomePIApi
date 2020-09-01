package dao

import javax.inject.Inject
import models.{DBFile, File}
import play.api.libs.Files
import play.api.mvc.MultipartFormData
import manager.{BucketManager, DBManager}
import play.libs.Json

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class FileDAO @Inject()(dbManager: DBManager) {
  def add(file: MultipartFormData.FilePart[Files.TemporaryFile], userId: String): Unit = {
    val fileModel = File.apply(file, userId)
    BucketManager.upload(fileModel)
    val fileDB = DBFile.apply(fileModel, userId)
    dbManager.insert[DBFile](fileDB)
  }

  def remove(fileName: String): Unit = {

  }

  def ls(path: String, userId: String): Future[List[DBFile]] = {
    val filter = DBFile.lsFilterJson(path, userId)
    dbManager.findAll[DBFile](filter).list()
  }
}
