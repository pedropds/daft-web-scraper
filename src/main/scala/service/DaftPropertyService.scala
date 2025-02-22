package service

import akka.actor.ActorSystem
import akka.stream.SystemMaterializer

import scala.concurrent.ExecutionContext
import scraper.DaftPropertyScraper

import scala.concurrent.Await
import scala.concurrent.duration.*

// Coordinator service to fetch places
object DaftPropertyService extends DaftServiceTrait{

  def getProperties(mainLink: String): List[String] = {
    // If cache is not valid, scrape from the web
    // Set up an ActorSystem and ExecutionContext
    implicit val system: ActorSystem = ActorSystem("DaftScraperSystem")
    implicit val materializer: SystemMaterializer = SystemMaterializer(system)
    implicit val ec: ExecutionContext = system.dispatcher // Implicit ExecutionContext

    val links: List[String] = Await.result(DaftPropertyScraper.fetchPropertyLinks(mainLink), Duration.Inf)
    shutdown()

    links
  }

}
