package dbModels

import java.time.Instant
import constants.DBHistoryConstants
import cn.playscala.mongo.annotations.Entity
import javax.inject.Singleton

@Singleton
@Entity("file")
case class DBHistory(
  _id: String,
  action: String,
  fileName: String
)

object DBHistory {

}