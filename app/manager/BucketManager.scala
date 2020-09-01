package manager

import models.File
import utils.Minio

object BucketManager {
  val defaultBucket = "home-storage"

  def lsPath(path: String, bucketName: String = defaultBucket): Unit = {

  }

  def download(path: String, bucketName: String = defaultBucket): Unit = {

  }

  def rmPath(path: String, bucketName: String = defaultBucket): Unit = {

  }

  def upload(file: File, bucketName: String = defaultBucket): Unit = {
    Minio.transaction(file, Minio.uploadObject)
  }
}
