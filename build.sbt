ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.4"

lazy val root = (project in file("."))
  .settings(
    name := "ScalaWebScraper",
    libraryDependencies ++= Seq(
      "org.jsoup" % "jsoup" % "1.18.3",
      "com.softwaremill.sttp.client3" %% "core" % "3.10.2"
    )
  )
