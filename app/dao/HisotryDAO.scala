package dao

import javax.inject.Inject
import managers.{DBManager}
import dbModels.{DBHistory}
import play.api.libs.json.{JsObject, Json}


class HisotryDAO @Inject()(dbManager: DBManager) {
  def getLatestByUserId() = {
    val filter: JsObject = Json.obj(
      "$limit" -> 5,
      "$sort" -> "date"
    )
    dbManager.findAll[DBHistory](filter)
  }
}