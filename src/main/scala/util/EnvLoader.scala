package util

import java.io.FileInputStream
import java.util.Properties

object EnvLoader {
  def loadEnv(): Map[String, String] = {
    val props = new Properties()
    val envFile = new FileInputStream(".env")
    props.load(envFile)
    envFile.close()

    props.stringPropertyNames()
      .toArray
      .map(_.toString)
      .map(key => key -> props.getProperty(key))
      .toMap
  }
}
