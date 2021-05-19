name += """HomePiApi"""

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.13"

initialize ~= { _ =>
  System.setProperty("org.mongodb.async.type", "netty")
}

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
libraryDependencies += "cn.playscala" % "play-mongo_2.12" % "0.3.1"
libraryDependencies += "io.minio" % "minio" % "7.1.4"
libraryDependencies += "io.netty" % "netty-all" % "4.1.34.Final"
PlayKeys.fileWatchService := play.dev.filewatch.FileWatchService.jdk7(play.sbt.run.toLoggerProxy(sLog.value))

libraryDependencies ++= Seq(
  "com.pauldijou" %% "jwt-play" % "4.3.0",
  "com.pauldijou" %% "jwt-core" % "4.3.0",
)

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
