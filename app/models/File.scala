package models

import java.time.Instant
import java.util.Date

import cn.playscala.mongo.annotations.Entity
import javax.inject.Singleton

@Singleton
@Entity("file")
case class File(id: Int,
                name: String,
                size: Float,
                extension: String,
                creationDate: Instant,
                lastUpdate: Date,
               )