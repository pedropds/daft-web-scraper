import scraper.{DaftPropertyScraper, Place}
import service.{DaftPlaceService, DaftPropertyService, TerminalService}

import java.io.{BufferedWriter, FileWriter}

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

    val properties: List[String] = DaftPropertyService.getProperties(searchUrl)

    //println(properties)
    println(properties.size)

    // Write properties to a file
    val outputFile = "properties_list.txt"
    val writer = new BufferedWriter(new FileWriter(outputFile))

    try {
      properties.foreach { property =>
        writer.write(property)
        writer.newLine()
      }
      println(s"Properties successfully written to $outputFile")
    } catch {
      case e: Exception => println(s"Error writing to file: ${e.getMessage}")
    } finally {
      writer.close()
    }
  }

}

