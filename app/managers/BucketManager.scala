package managers

import models.File
import utils.Minio

object BucketManager {
  val defaultBucket = "home-storage"

  def lsPath(path: String, bucketName: String = defaultBucket): Unit = {

  }

  def download(path: String, bucketName: String = defaultBucket): Unit = {

  }

  def remove(path: String, bucketName: String = defaultBucket): Unit = {
    Minio.transactionBlobString(path, Minio.removeObject)
  }

  def upload(file: File, bucketName: String = defaultBucket): Unit = {
    Minio.transaction(file, Minio.uploadObject)
  }
}
