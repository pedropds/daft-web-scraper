import WebScraper._

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
object Main {
  def main(args: Array[String]): Unit = {
    val url = "https://en.wikipedia.org/wiki/World_War_II"

    // Call the scrape function and handle the result
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
  }
}


