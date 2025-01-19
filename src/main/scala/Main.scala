import akka.actor.ActorSystem
import akka.stream.SystemMaterializer
import scraper.{DaftPlaceScraper, Place}

import scala.util.{Failure, Success}
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.concurrent.ExecutionContext
import service.DaftPlaceService
import scala.concurrent.ExecutionContext.Implicits.global

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
object Main {

  private def getOptions(): Unit = {
    // Call the getPlaces method
    val query = "Dublin"
    DaftPlaceService.getPlaces(query).onComplete {
      case Success(places) =>
        println(s"Successfully fetched places: $places")
      case Failure(exception) =>
        println(s"Failed to fetch places: $exception")
    }
  }


  def main(args: Array[String]): Unit = {

    getOptions()

    val url = "https://en.wikipedia.org/wiki/World_War_II"

    // Call the scrape function and handle the result
    /*
    scrape(url) match {
      case Right((title, links)) =>
        println(s"Page Title: $title")
        println("Links:")
        links.foreach { case (href, text) =>
          println(s"- $href | $text")
        }
      case Left(error) =>
        println(s"Failed to scrape the URL. Error: $error")
    }
     */
  }

}

