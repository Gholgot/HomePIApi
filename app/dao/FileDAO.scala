package dao

import com.mongodb.client.result.{DeleteResult}
import dbModels.{DBFile, DBHistory}
import javax.inject.Inject
import managers.{BucketManager, DBManager}
import models.{File, HistoryAction}
import play.api.libs.Files
import play.api.mvc.MultipartFormData
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json.{JsObject}
import scala.concurrent.Future

class FileDAO @Inject()(dbManager: DBManager) {
  def add(file: MultipartFormData.FilePart[Files.TemporaryFile], userId: String, maybeFolderId: Option[String]): Unit = {
    val fileModel: File = File.apply(file, userId, maybeFolderId)
    BucketManager.upload(fileModel)
    val fileDB = DBFile.apply(fileModel, userId)
    dbManager.insert[DBFile](fileDB)
    val dbHistory = DBHistory.apply(fileDB._id, HistoryAction.CREATE)
    dbManager.insert[DBHistory](dbHistory)
  }

  def remove(fileId: String, userId: String, maybeFolderId: Option[String]): Unit = {
    val folderId: String = maybeFolderId match {
      case Some(id) => id
      case None => File.defaultFolderId
    }

    val dbSearchFilter: JsObject = DBFile.findByBlobStringFilterJson(fileId, folderId, userId)

    for {
      maybeDBFile <- dbManager.findFirst[DBFile](dbSearchFilter)
      dbDeleteResult <- dbManager.delete[DBFile](dbSearchFilter)
    } yield {
      if(dbDeleteResult.wasAcknowledged()) {
        maybeDBFile match {
          case Some(dbFile) => BucketManager.remove(File.buildBucketPath(userId, folderId, dbFile.name))
          case None => throw new Exception("Couldn't find the entity to delete")
        }
      } else {
        throw new Exception("DB DELETE FAILED")
      }
    }
  }

  def ls(folderId: Option[String], userId: String): Future[List[DBFile]] = {
    val filter = DBFile.lsFilterJson(folderId.getOrElse(File.defaultFolderId), userId)
    dbManager.findAll[DBFile](filter).list()
  }
}
