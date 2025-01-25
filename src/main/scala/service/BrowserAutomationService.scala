package service

import org.openqa.selenium.{By, WebDriver}
import org.openqa.selenium.chrome.ChromeDriver
import util.EnvLoader

import java.time.Duration

object BrowserAutomationService {

  def sendEmailToProperty(propertyLink: String): Unit = {
    // Load environment variables
    val env = EnvLoader.loadEnv()
    val chromeDriverPath = env("CHROME_DRIVER_PATH") // Get the value from the .env file

    if(chromeDriverPath == null || chromeDriverPath.isEmpty)
      throw RuntimeException("Chrome Driver path is null or empty")

    // Set up the ChromeDriver
    System.setProperty("webdriver.chrome.driver", chromeDriverPath)
    val driver: WebDriver = new ChromeDriver()

    try {
      driver.get(propertyLink)

      // Wait for the page to load
      driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10))
      Thread.sleep(1000) // Add a 1-second delay

      // Step 2: Click the email button
      val emailButton = driver.findElement(By.cssSelector("button[data-testid='message-btn']"))
      emailButton.click()
      Thread.sleep(1000) // Add a 1-second delay

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
