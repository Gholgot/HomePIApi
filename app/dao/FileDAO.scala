package dao

import dbModels.DBFile
import javax.inject.Inject
import managers.{BucketManager, DBManager}
import models.File
import play.api.libs.Files
import play.api.mvc.MultipartFormData

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
