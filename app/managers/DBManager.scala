package managers

import cn.playscala.mongo.Mongo
import com.mongodb.client.result.{DeleteResult, UpdateResult}
import javax.inject.Inject
import play.api.libs.json.JsObject
import scala.reflect.runtime.universe._

import scala.concurrent.Future
import scala.reflect.ClassTag

class DBManager @Inject()(mongo: Mongo) {
  def findFirst[T: ClassTag: TypeTag](filter: JsObject): Future[Option[T]] = {
    mongo.find[T](filter).first
  }

  def delete[T: ClassTag: TypeTag](filter: JsObject): Future[DeleteResult] = {
    mongo.deleteOne[T](filter)
  }

  def insert[T: ClassTag: TypeTag](toInsert: T) = {
    mongo.insertOne[T](toInsert)
  }

  def update[T: ClassTag: TypeTag](filter: JsObject, entity: JsObject): Future[UpdateResult] = {
    mongo.updateOne[T](filter, entity)
  }

  def findAndUpdate[T: ClassTag: TypeTag](filter: JsObject, entity: JsObject): Future[Option[T]] = {
    mongo.findOneAndUpdate[T](filter, entity)
  }

  def findAll[T: ClassTag: TypeTag](filter: JsObject) = {
    mongo.find[T](filter)
  }
}
