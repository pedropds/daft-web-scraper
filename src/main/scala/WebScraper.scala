import sttp.client3._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import scala.jdk.CollectionConverters._

object WebScraper {
  // Fetches the HTML content of a URL
  private def fetchHtml(url: String): Either[String, String] = {
    val backend = HttpURLConnectionBackend()
    val request = basicRequest.get(uri"$url")
    val response = request.send(backend)
    response.body
  }

  // Parses the HTML and extracts the page title
  private def extractTitle(html: String): String = {
    val doc: Document = Jsoup.parse(html)
    doc.title()
  }

  // Extracts links from the HTML as a List of tuples (href, text)
  private def extractLinks(html: String): List[(String, String)] = {
    val doc: Document = Jsoup.parse(html)
    doc.select("a[href]").asScala.toList.map(link => (link.attr("href"), link.text()))
  }

  // Performs the entire scrape process and returns structured data
  def scrape(url: String): Either[String, (String, List[(String, String)])] = {
    fetchHtml(url).map { html =>
      val title = extractTitle(html)
      val links = extractLinks(html)
      (title, links)
    }
  }
}
