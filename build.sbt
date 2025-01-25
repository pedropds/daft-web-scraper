ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.4"

lazy val root = (project in file("."))
  .settings(
    name := "ScalaWebScraper",
    libraryDependencies ++= Seq(
      "org.jsoup" % "jsoup" % "1.18.3",
      "com.softwaremill.sttp.client3" %% "core" % "3.10.2",
      "com.typesafe.akka" %% "akka-http" % "10.5.3",      // HTTP client
      "com.typesafe.akka" %% "akka-stream" % "2.8.8",     // Streaming support
      "io.spray" %% "spray-json" % "1.3.6",               // JSON parser
      "com.typesafe.play" %% "play-json" % "2.10.6",
      // selenium for browser automation process.
      "org.seleniumhq.selenium" % "selenium-java" % "4.28.1",
      "org.seleniumhq.selenium" % "selenium-api" % "4.28.1",
      "org.seleniumhq.selenium" % "selenium-http" % "4.28.1"
    )
  )
