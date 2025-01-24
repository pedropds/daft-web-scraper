package scraper

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.*

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import scala.util.{Failure, Success}

import scala.concurrent.duration.*

object DaftPropertyScraper {

  private val PageSize = 20 // Number of results per page

  def createDaftUrl(selectedPlaces: List[Place]): String = {
    // base url for daft renting properties
    val baseUrl = "https://www.daft.ie/property-for-rent"

    selectedPlaces match {
      case Nil =>
        throw new IllegalArgumentException("No places selected. Cannot construct URL.")
      case List(singlePlace) =>
        // Single place: Append the place's `displayValue` to the base URL
        s"$baseUrl/${singlePlace.displayValue.replace(" ", "-").toLowerCase}"
      case multiplePlaces =>
        // Multiple places: Use the base URL and add query parameters for each location
        val queryParams = multiplePlaces.map { place =>
          s"location=${place.displayValue.replace(" ", "-").toLowerCase}"
        }.mkString("&")
        s"$baseUrl/ireland?$queryParams"
    }
  }

  def fetchPropertyLinks(baseUrl: String)(implicit system: ActorSystem, ec: ExecutionContext): Future[List[String]] = {
    val pageSize = 20
    val currentUrl = s"$baseUrl?pageSize=$pageSize&from=0" // Starting URL
    var previousFrom: Option[Int] = None // To store the last `from` value
    val accumulatedLinks: List[String] = List.empty // To accumulate the property links

    // Sequentially process pages using a loop with Future chaining
    def processPages(url: String): Future[List[String]] = {
      Http().singleRequest(HttpRequest(uri = url)).flatMap { response =>
        response.entity.toStrict(5.seconds).map(_.data.utf8String).flatMap { html =>
          val document: Document = Jsoup.parse(html)
          val propertyLinks: List[String] = extractLinks(document)

          // Extract the "Next" page link and get the value of `from`
          val nextPageHref = document.select("a[rel=next]").attr("href")

          // If there is no "Next" page link or we should stop, return the accumulated links
          if (nextPageHref.isEmpty) {
            println(s"No more pages, returning accumulated links.")
            Future.successful(accumulatedLinks ++ propertyLinks)
          } else {
            // Extract the `from` value from the next page link
            val fromValue = nextPageHref.split("from=")(1).split("&")(0).toInt

            // If the fromValue is lower than previousFrom, stop the loop
            previousFrom match {
              case Some(prev) if fromValue < prev =>
                println("Reached a previously visited page, stopping pagination.")
                Future.successful(accumulatedLinks ++ propertyLinks) // Return accumulated links and stop
              case _ =>
                // Update the `previousFrom` for the next iteration
                previousFrom = Some(fromValue)

                // Construct the next page URL with `pageSize=20` and the correct `from`
                val nextPageUrl = s"$baseUrl?pageSize=$pageSize&from=$fromValue"
                println(s"Fetching next page: $nextPageUrl")

                // Continue to the next page by chaining the future
                processPages(nextPageUrl).map { nextLinks =>
                  accumulatedLinks ++ propertyLinks ++ nextLinks // Combine current and next page links
                }
            }
          }
        }
      }.recoverWith {
        case ex: Exception =>
          Future.failed(new RuntimeException(s"Failed to fetch or parse $url: ${ex.getMessage}", ex))
      }
    }

    // Start the process from the base URL
    processPages(currentUrl)
  }

  private def extractLinks(document: Document): List[String] = {
    val baseUrl = "https://www.daft.ie"

    // Select all <li> elements with `data-testid` that contain the property links
    val propertyElements: Elements = document.select("li[data-testid] a[href]")

    // Extract the href attributes, filter for `/for-rent/`, and prepend the base URL
    propertyElements
      .eachAttr("href")
      .asScala
      .toList
      .filter(_.contains("/for-rent/")) // Only keep links with `/for-rent/`
      .map(link => s"$baseUrl$link")
  }
}
