package scraper

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.*
import akka.http.scaladsl.model.headers.*
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.SystemMaterializer
import scraper.PlaceJsonProtocol.jsonFormat13
import spray.json.*

import scala.concurrent.{ExecutionContext, Future}

// Define JSON case classes
case class PropertyCount(
                          residentialForRent: Option[Int],
                          residentialForSale: Option[Int],
                          sharing: Option[Int],
                          commercialForSale: Option[Int],
                          commercialToRent: Option[Int],
                          parkingForSale: Option[Int],
                          parkingToRent: Option[Int],
                          newHomes: Option[Int],
                          holidayHomes: Option[Int],
                          studentAccommodationForRent: Option[Int],
                          studentAccommodationToShare: Option[Int],
                          recent: Option[Int],
                          sold: Option[Int]
                        )

case class Place(
                  sequentialId: Int,
                  id: String,
                  displayName: String,
                  displayValue: String,
                  propertyCount: PropertyCount
                )

// JSON Protocol for Spray JSON
object PlaceJsonProtocol extends DefaultJsonProtocol {
  implicit val propertyCountFormat: RootJsonFormat[PropertyCount] = jsonFormat13(PropertyCount.apply)

  implicit val placeFormat: RootJsonFormat[Place] = new RootJsonFormat[Place] {
    override def read(json: JsValue): Place = {
      json.asJsObject.getFields("id", "displayName", "displayValue", "propertyCount") match {
        case Seq(JsString(id), JsString(displayName), JsString(displayValue), propertyCount) =>
          Place(
            id = id,
            displayName = displayName,
            displayValue = displayValue,
            propertyCount = propertyCount.convertTo[PropertyCount],
            sequentialId = 0 // Temporary, will be assigned dynamically later
          )
        case _ =>
          throw DeserializationException("Place JSON is malformed or missing fields")
      }
    }

    override def write(place: Place): JsValue = JsObject(
      "id" -> JsString(place.id),
      "displayName" -> JsString(place.displayName),
      "displayValue" -> JsString(place.displayValue),
      "propertyCount" -> place.propertyCount.toJson,
      "sequentialId" -> JsNumber(place.sequentialId)
    )
  }
}

object DaftPlaceScraper {

  import PlaceJsonProtocol.*

  // Create a functional interface with the HTTP client and JSON parsing
  def fetchAreas(query: String)(implicit ec: ExecutionContext, system: ActorSystem, materializer: SystemMaterializer): Future[List[Place]] = {
    val apiUrl = "https://gateway.daft.ie/old/v1/autocomplete"
    val requestBody = HttpEntity(ContentTypes.`application/json`, s"""{"text": "$query"}""")

    Http()
      .singleRequest(
        HttpRequest(
          method = HttpMethods.POST,
          uri = apiUrl,
          entity = requestBody,
          headers = List(
            RawHeader("Accept", "application/json"),
            RawHeader("Origin", "https://www.daft.ie"),
            RawHeader("Platform", "web"),
            RawHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"),
            RawHeader("brand", "daft"),
            RawHeader("sec-ch-ua", "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\""),
            RawHeader("sec-ch-ua-platform", "\"macOS\"")
          ),
        )
      )
      .flatMap { response =>
        Unmarshal(response.entity).to[String].map { responseJson =>
          val places = responseJson.parseJson.convertTo[List[Place]]
          assignSequentialIds(places)
        }
      }
      .recoverWith {
        case ex: Exception =>
          Future.failed(new RuntimeException(s"Error fetching areas: ${ex.getMessage}", ex))
      }
  }

  // Assign sequential IDs after deserialization
  private def assignSequentialIds(places: List[Place]): List[Place] = {
    places.zipWithIndex.map { case (place, index) =>
      place.copy(sequentialId = index + 1)
    }
  }

}
