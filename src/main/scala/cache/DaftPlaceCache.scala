package cache

import scraper.Place
import spray.json.*

import java.io.{File, PrintWriter}
import java.time.LocalDateTime
import scala.io.Source

case class CachedData(timestamp: String, data: List[Place])

// JSON Protocol for Cached Data
object CacheJsonProtocol extends DefaultJsonProtocol {
  // Existing formats for HTTP responses
  import scraper.PlaceJsonProtocol.*

  // Additional format for caching
  implicit val cachedDataFormat: RootJsonFormat[CachedData] = jsonFormat2(CachedData.apply)
}


object DaftPlaceCache {
  import CacheJsonProtocol.*

  // Ensure the cache folder exists
  private val cacheFolder = new File(".cache")
  if (!cacheFolder.exists()) {
    cacheFolder.mkdir() // Create the folder if it doesn't exist
  }

  private val cacheFile = new File(cacheFolder, "places_cache.json")

  // Check if the cache is valid (assumes 1-day validity)
  def isCacheValid: Boolean = {
    if (!cacheFile.exists()) return false

    val source = Source.fromFile(cacheFile)
    try {
      val cachedData = source.mkString.parseJson.convertTo[CachedData]
      val cacheTime = LocalDateTime.parse(cachedData.timestamp)
      val now = LocalDateTime.now()
      cacheTime.plusDays(1).isAfter(now)
    } catch {
      case _: Exception => false // Invalid cache on error
    } finally {
      source.close()
    }
  }

  // Read the cache file and return the data
  def readCache: List[Place] = {
    if (!cacheFile.exists()) throw new RuntimeException("Cache file does not exist")
    val source = Source.fromFile(cacheFile)
    try {
      source.mkString.parseJson.convertTo[CachedData].data
    } finally {
      source.close()
    }
  }

  // Write the data to the cache file
  def writeCache(data: List[Place]): Unit = {
    val cachedData = CachedData(LocalDateTime.now().toString, data)
    val json = cachedData.toJson.prettyPrint
    val writer = new PrintWriter(cacheFile)
    try {
      writer.write(json)
    } finally {
      writer.close()
    }
  }
}
