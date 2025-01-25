package service

import org.openqa.selenium.{By, WebDriver}
import org.openqa.selenium.chrome.ChromeDriver

import java.time.Duration

object BrowserAutomationService {

  def sendEmailToProperty(propertyLink: String): Unit = {
    // Set up the ChromeDriver (ensure you have the right driver installed)
    System.setProperty("webdriver.chrome.driver", "/opt/homebrew/bin/chromedriver")
    val driver: WebDriver = new ChromeDriver()

    try {
      driver.get(propertyLink)

      // Wait for the page to load
      driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10))
      Thread.sleep(1000) // Add a 1-second delay

      // Step 2: Click the email button
      val emailButton = driver.findElement(By.cssSelector("button[data-testid='message-btn']"))
      emailButton.click()
      Thread.sleep(1500) // Add a 1-second delay

      // Step 3: Paste text into input boxes
      val emailInput = driver.findElement(By.id("email-input-id")) // Replace with actual ID
      emailInput.sendKeys("your_email@example.com")
      Thread.sleep(1000) // Add a 1-second delay

      val messageInput = driver.findElement(By.id("message-input-id")) // Replace with actual ID
      messageInput.sendKeys("Your custom message goes here.")
      Thread.sleep(1000) // Add a 1-second delay

      // Optionally, submit the form
      val submitButton = driver.findElement(By.cssSelector(".submit-button-class")) // Replace with actual selector
      submitButton.click()
      Thread.sleep(1000) // Add a 1-second delay

      println("Automation completed successfully.")

    } catch {
      case e: Exception =>
        e.printStackTrace()
    } finally {
      // Step 4: Close the browser
      driver.quit()
    }
  }
}
