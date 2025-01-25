import scraper.{DaftPropertyScraper, Place}
import service.{BrowserAutomationService, DaftPlaceService, DaftPropertyService, TerminalService}

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
object Main {

  def main(args: Array[String]): Unit = {
    // cache is only available after we scrape Future
    val places: List[Place] = DaftPlaceService.getPlacesByScrapingDaft("Dublin")

    if (places.isEmpty) {
      println("No places found.")
      return
    }

    println(s"Found ${places.length} places. Starting interactive session...")

    // Start the interactive filtering and selection process
    val filteredPlaces: List[Place] = TerminalService.interactiveFilterAndSelectPlaces(places)

    val searchUrl = DaftPropertyScraper.createDaftUrl(filteredPlaces)

    println(searchUrl)

    val properties = DaftPropertyService.getProperties(searchUrl)

    //println(properties)
    println(properties.size)

    // find first property in list
    val property = properties.find(p => 1 == 1).get

    BrowserAutomationService.sendEmailToProperty(property)
  }

}

