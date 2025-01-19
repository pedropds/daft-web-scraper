package service

import akka.actor.ActorSystem
import akka.stream.SystemMaterializer

import scala.concurrent.{ExecutionContext, Future}
import cache.DaftPlaceCache
import scraper.DaftPlaceScraper
import scraper.Place
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success}

// Coordinator service to fetch places
object DaftPlaceService {

  // Method to get Places
  def getPlaces(query: String): Future[List[Place]] = {
    // Set up an ActorSystem and ExecutionContext
    implicit val system: ActorSystem = ActorSystem("DaftScraperSystem")
    implicit val materializer: SystemMaterializer = SystemMaterializer(system)
    implicit val ec: ExecutionContext = system.dispatcher // Implicit ExecutionContext

    if (DaftPlaceCache.isCacheValid) {
      // If cache is valid, return cached data
      Future.successful(DaftPlaceCache.readCache)
    } else {
      // If cache is not valid, scrape from the web
      fetchPlacesFromWeb(query).andThen {
        case _ =>
          // After fetching, cache the result
          DaftPlaceService.cachePlaces(query)
      }
    }
  }

  // Method to fetch Places from the web scraper
  private def fetchPlacesFromWeb(query: String)(implicit ec: ExecutionContext, system: ActorSystem, materializer: SystemMaterializer): Future[List[Place]] = {
    val areasFuture: Future[List[Place]] = DaftPlaceScraper.fetchAreas(query)

    areasFuture.onComplete {
      case Success(places) =>
        println(s"List of places: $places")
      case Failure(exception) =>
        println(s"Request failed with exception: $exception")
    }

    areasFuture
  }

  // Method to cache the Places data
  private def cachePlaces(query: String)(implicit ec: ExecutionContext, system: ActorSystem, materializer: SystemMaterializer): Unit = {
    // Await the result of the scraping
    val places = Await.result(DaftPlaceService.fetchPlacesFromWeb(query), 30.seconds) // Use your desired timeout

    // Cache the result
    DaftPlaceCache.writeCache(places)
    println(s"Cached places: $places")

    // Only shutdown after all operations are complete
    shutdown()
  }

  // Shutdown method that requires ExecutionContext to handle Future completion
  private def shutdown()(implicit system: ActorSystem, ec: ExecutionContext): Unit = {
    system.terminate().onComplete {
      case Success(_) => println("ActorSystem terminated successfully.")
      case Failure(error) => println(s"Failed to terminate ActorSystem: ${error.getMessage}")
    }
  }
}
