import scraper.Place
import service.TerminalService
import service.DaftPlaceService

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
object Main {

  def main(args: Array[String]): Unit = {
    // cache is only available after we scrape Future
    val places: List[Place] = DaftPlaceService.getPlacesByScrapingDaft("Dublin")

    if (places.isEmpty) {
      println("No places found.")
      return
    }

    // Start the interactive filtering and selection process
    println(s"Found ${places.length} places. Starting interactive session...")

    TerminalService.interactiveFilterAndSelectPlaces(places)
  }

}

