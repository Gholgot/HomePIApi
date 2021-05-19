package utils

import java.io.IOException
import java.security.{InvalidKeyException, NoSuchAlgorithmException}

import io.minio.errors.MinioException
import io.minio._
import models.File

object Minio {
  lazy val client: MinioClient =
    MinioClient.builder().endpoint("http://192.168.1.30:9000/").credentials("minioadmin", "minioadmin").build()
    
  @throws[NoSuchAlgorithmException]
  @throws[IOException]
  @throws[InvalidKeyException]
  def transaction(file: File, action: (File) => Unit): Unit = {
    val isExist = client.bucketExists(BucketExistsArgs.builder().bucket("home-storage").build())
    try {
      if (!isExist) {
        client.makeBucket(MakeBucketArgs.builder().bucket("home-storage").build())
      }
      action(file)
    } catch {
      case e: MinioException =>
        System.out.println("Error occurred: " + e)
    }
  }

  def transactionBlobString(path: String, action: (String) => Unit): Unit = {
    val isExist = client.bucketExists(BucketExistsArgs.builder().bucket("home-storage").build())
    try {
      if (!isExist) {
        client.makeBucket(MakeBucketArgs.builder().bucket("home-storage").build())
      }
      action(path)
    } catch {
      case e: MinioException =>
        System.out.println("Error occurred: " + e)
    }
  }

  def uploadObject(file: File): Unit = {
    file.absolutePath match {
      case Some(path) => {
        client.uploadObject(
          UploadObjectArgs.builder()
            .bucket("home-storage")
            .filename(path)
            .contentType(file.contentType)
            .`object`(s"${file.userId}/${file.folderId}~~${file.name}")
            .build()
        )
      }
      case None       => throw new Exception("PATH ERROR")
    }
  }

  def putObject(file: File): Unit = {
    file.inputStream match {
      case Some(stream) =>
        client.putObject(
          PutObjectArgs.builder()
            .bucket("home-storage")
            .`object`(file.name)
            .contentType(file.contentType)
            .stream(stream, file.size, 0)
            .build()
        )
      case None         => throw new Exception("MISSING INPUTSTREAM")
    }
  }

  def removeObject(path: String): Unit = {
    client.removeObject(
      RemoveObjectArgs.builder()
      .bucket("home-storage")
      .`object`(path)
      .build()
    )
  }

  def createFolder(folderName: String): Unit = {
    client.putObject(
      PutObjectArgs.builder()
        .bucket("home-storage")
        .`object`(folderName)
        .build()
    )
  }
}
