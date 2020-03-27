name := """HomePiApi"""
organization := "com.example"

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

initialize ~= { _ =>
  System.setProperty("org.mongodb.async.type", "netty")
}

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "cn.playscala" % "play-mongo_2.12" % "0.3.0"
libraryDependencies += "io.netty" % "netty-all" % "4.1.17.Final"

libraryDependencies ++= Seq(
  "com.pauldijou" %% "jwt-play" % "0.19.0",
  "com.pauldijou" %% "jwt-core" % "0.19.0",
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
