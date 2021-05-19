package dbModels

import java.util.UUID
import java.time.Instant

import java.time.Instant
import cn.playscala.mongo.annotations.Entity
import javax.inject.Singleton
import models.HistoryAction

@Singleton
@Entity("file-history")
case class DBHistory(
  _id: String,
  action: String,
  fileId: String,
  date: Instant)

object DBHistory {
  def apply(fileId: String, action: String): DBHistory = {
    DBHistory(UUID.randomUUID().toString, action, fileId, Instant.now())
  }
}