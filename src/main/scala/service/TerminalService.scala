package service

import scraper.Place
import scala.io.StdIn
import scala.annotation.tailrec

object TerminalService {
  
  @tailrec
  def interactiveFilterAndSelectPlaces(
                                          originalPlaces: List[Place],
                                          filteredPlaces: List[Place] = Nil,
                                          chosenPlaces: List[Place] = Nil,
                                          currentPage: Int = 1
                                        ): List[Place] = {
    val placesToShow = if (filteredPlaces.nonEmpty) filteredPlaces else originalPlaces
    val totalPages = (placesToShow.length + 19) / 20 // 20 places per page
    val pagedPlaces = placesToShow.slice((currentPage - 1) * 20, currentPage * 20)

    // Display current page of places
    println(s"\nAvailable Places (Page $currentPage of $totalPages):")
    pagedPlaces.foreach(place =>
      println(s"${place.sequentialId}. ${place.displayName} (${place.propertyCount.residentialForRent.getOrElse(0)} rentals)")
    )

    // Show selected places
    if (chosenPlaces.nonEmpty) {
      println("\nChosen Places:")
      chosenPlaces.foreach(place => println(s"${place.sequentialId}. ${place.displayName}"))
    }

    // Display options
    println("\nOptions:")
    println("1. Search/filter places by name")
    println("2. Select multiple places by numbers (comma-separated)")
    println("3. De-select places by numbers (comma-separated)")
    println("4. Clear filters")
    println("5. Navigate pages (n for next, p for previous)")
    println("6. Finish selection and return chosen places")

    print("\nChoose an option: ")
    StdIn.readLine().trim match {
      case "1" =>
        print("Enter a search term: ")
        val searchTerm = StdIn.readLine().trim.toLowerCase
        val newFilteredPlaces = originalPlaces.filter(_.displayName.toLowerCase.contains(searchTerm))
        if (newFilteredPlaces.isEmpty) {
          println(s"No places match '$searchTerm'. Try again.")
        }
        interactiveFilterAndSelectPlaces(originalPlaces, newFilteredPlaces, chosenPlaces)

      case "2" =>
        print("Enter the numbers of the places to select (comma-separated): ")
        val selectedPlaces = try {
          val selectedIds = StdIn.readLine()
            .split(",")
            .map(_.trim.toInt)
            .filter(id => originalPlaces.exists(_.sequentialId == id))
          selectedIds.map(id => originalPlaces.find(_.sequentialId == id).get).toList
        } catch {
          case _: NumberFormatException =>
            println("Invalid input. Please enter valid numbers separated by commas.")
            Nil
        }

        if (selectedPlaces.nonEmpty) {
          println("\nYou selected:")
          selectedPlaces.foreach(place => println(s"- ${place.displayName}"))
          val updatedChosenPlaces = chosenPlaces ++ selectedPlaces.distinct.filterNot(chosenPlaces.contains)
          interactiveFilterAndSelectPlaces(originalPlaces, filteredPlaces, updatedChosenPlaces, currentPage)
        } else {
          println("No valid selections were made. Please try again.")
          interactiveFilterAndSelectPlaces(originalPlaces, filteredPlaces, chosenPlaces, currentPage)
        }

      case "3" =>
        print("Enter the numbers of the places to de-select (comma-separated): ")
        val deselectedPlaces = try {
          val deselectedIds = StdIn.readLine()
            .split(",")
            .map(_.trim.toInt)
            .filter(id => chosenPlaces.exists(_.sequentialId == id))
          deselectedIds.map(id => chosenPlaces.find(_.sequentialId == id).get).toList
        } catch {
          case _: NumberFormatException =>
            println("Invalid input. Please enter valid numbers separated by commas.")
            Nil
        }

        if (deselectedPlaces.nonEmpty) {
          println("\nYou de-selected:")
          deselectedPlaces.foreach(place => println(s"- ${place.displayName}"))
          val updatedChosenPlaces = chosenPlaces.diff(deselectedPlaces)
          interactiveFilterAndSelectPlaces(originalPlaces, filteredPlaces, updatedChosenPlaces, currentPage)
        } else {
          println("No valid de-selections were made. Please try again.")
          interactiveFilterAndSelectPlaces(originalPlaces, filteredPlaces, chosenPlaces, currentPage)
        }

      case "4" =>
        println("Filters cleared. Showing all places.")
        interactiveFilterAndSelectPlaces(originalPlaces, Nil, chosenPlaces)

      case "5" =>
        print("Navigate pages: (n for next, p for previous): ")
        StdIn.readLine().trim.toLowerCase match {
          case "n" if currentPage < totalPages =>
            interactiveFilterAndSelectPlaces(originalPlaces, filteredPlaces, chosenPlaces, currentPage + 1)
          case "p" if currentPage > 1 =>
            interactiveFilterAndSelectPlaces(originalPlaces, filteredPlaces, chosenPlaces, currentPage - 1)
          case _ =>
            println("Invalid page navigation or no more pages in that direction.")
            interactiveFilterAndSelectPlaces(originalPlaces, filteredPlaces, chosenPlaces, currentPage)
        }

      case "6" =>
        println("Finishing selection...")
        println("\nChosen places:")
        chosenPlaces.foreach(place => println(s"- ${place.displayName}"))
        chosenPlaces

      case _ =>
        println("Invalid option. Please try again.")
        interactiveFilterAndSelectPlaces(originalPlaces, filteredPlaces, chosenPlaces, currentPage)
    }
  }
}
