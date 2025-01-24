package scraper


object MainPropertyScraper {
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
}
