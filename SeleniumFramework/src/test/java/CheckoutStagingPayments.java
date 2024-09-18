import java.time.Duration;
import java.util.*;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CheckoutStagingPayments {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//careemPayWithSAR();
		madaWithBHDFX();
		AmexWithUSD();
		AmexWithKWD();
		VISAWithUSD();
		MCWithSAR();
		madaWithBHDFX();
		madaWithSAR();
		paypalWithUSD();
		paypalWithSAR();
		knetWithKWD();
		knetWithSAR();
		//benefitWithBHD();
		//benefitWithSAR();
	}
	
	public static void knetWithKWD() {
		ChromeOptions options = new ChromeOptions();
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable( LogType.PERFORMANCE, Level.ALL );
		options.setCapability( "goog:loggingPrefs", logPrefs );
		WebDriver driver = new ChromeDriver(options);
		
		driver.get("https://demo.staging.tap.company/v2/sdk/checkout");
	    driver.manage().window().setSize(new Dimension(1200, 859));
	    
	    // Select the currency
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[2]/div/div[6]/div/div")).click();
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul/li[1]")).click();
	    
	    // Start the checkout
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[3]/button")).click();

	    // Wait for the checkout iFrame to be loaded
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(20000));
	    WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div[2]/div/iframe")));
	    driver.switchTo().frame("tap-checkout-sdk-iframe");
	    
	    // Wait for the KNET chip to be rendered
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div[2]/div/div/div[1]/div/div/div/div[1]/div/div/div/h6[1]")));
	    List<WebElement> chipsElements = driver.findElements(By.xpath("//img[@class='MuiBox-root css-qmsygv']"));
	    for (WebElement webElement : chipsElements) {
	    	String imgSrc = webElement.getAttribute("src");
			if(!imgSrc.isEmpty() && imgSrc.contains("knet.svg")) {
				wait.until(ExpectedConditions.elementToBeClickable(webElement));
				webElement.click();
				break;
			}
		}
	    
	    // Start the knet payment
	    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div[2]/div/div/div[7]/div/div/div/div/button")));
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div[2]/div/div/div[7]/div/div/div/div/button")).click();
	    
	    // Wait for KNET to be loaded
	    driver.switchTo().parentFrame();
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/form/div[1]/div/div[2]/div[3]/div[1]/div[2]/select")));
	    
	    (new Select(driver.findElement(By.xpath("/html/body/form/div[1]/div/div[2]/div[3]/div[1]/div[2]/select")))).selectByValue("201825717889145|Knet Test Card [KNET1]|0.000|0.00|0.000");
	    wait.until(ExpectedConditions.elementToBeClickable(By.id("debitNumber")));
	    driver.findElement(By.id("debitNumber")).sendKeys("0000000001");
	    
	    (new Select(driver.findElement(By.xpath("/html/body/form/div[1]/div/div[2]/div[3]/div[3]/div[2]/select[1]")))).selectByValue("9");
	    (new Select(driver.findElement(By.xpath("/html/body/form/div[1]/div/div[2]/div[3]/div[3]/div[2]/select[2]")))).selectByValue("2025");
	    
	    driver.findElement(By.id("cardPin")).sendKeys("1234");
	    driver.findElement(By.id("proceed")).click();
	    
	    wait.until(ExpectedConditions.elementToBeClickable(By.id("proceedConfirm")));
	    driver.findElement(By.id("proceedConfirm")).click();
	    
	    // Fetch the charge id
	    wait.until(ExpectedConditions.urlContains("demo.staging"));
	    List<LogEntry> entries = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
	    System.out.println(entries.size() + " " + LogType.PERFORMANCE + " log entries found");
	     for (LogEntry entry : entries) {
	    	 try {
	    		 ObjectMapper mapper = new ObjectMapper();
	    		 JsonNode rootNode = mapper.readTree(entry.getMessage());
	    		 if(rootNode.has("message") &&
	    				 rootNode.findValue("message").has("params") &&
	    				 rootNode.findValue("message").findValue("params").has("documentURL")) {
	    			 String url = rootNode.findValue("message").findValue("params").findValue("documentURL").asText();
	    			 if(url.contains("tap_id")) {
	    				 String chargeID = url.split("tap_id=")[1];
	    				 String chargeStatus = getChargeStatus(chargeID);
	    				 System.out.println(((chargeStatus.equalsIgnoreCase("captured")) ? "✅" : "⚠️") + "KNET - KWD - POPUP - "+chargeID+" - "+getChargeStatus(chargeID));
	    				 break;
	    			 }
	    		 }
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}  
	     }
	    
	    driver.close();
	}
	
	public static void knetWithSAR() {
		ChromeOptions options = new ChromeOptions();
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable( LogType.PERFORMANCE, Level.ALL );
		options.setCapability( "goog:loggingPrefs", logPrefs );
		WebDriver driver = new ChromeDriver(options);
		
		driver.get("https://demo.staging.tap.company/v2/sdk/checkout");
	    driver.manage().window().setSize(new Dimension(1200, 859));
	    // Start the checkout
	    
	    
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[3]/button")).click();

	    // Wait for the checkout iFrame to be loaded
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(100000));
	    WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div[2]/div/iframe")));
	    driver.switchTo().frame("tap-checkout-sdk-iframe");
	    
	    // Wait for the KNET chip to be rendered
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div[2]/div/div/div[1]/div/div/div/div[1]/div/div/div/h6[1]")));
	    List<WebElement> chipsElements = driver.findElements(By.xpath("//img[@class='MuiBox-root css-qmsygv']"));
	    for (WebElement webElement : chipsElements) {
	    	String imgSrc = webElement.getAttribute("src");
			if(!imgSrc.isEmpty() && imgSrc.contains("knet.svg")) {
				wait.until(ExpectedConditions.elementToBeClickable(webElement));
				webElement.click();
				break;
			}
		}
	    
	    // Wait for the FX change to KWD widget is visible
	    WebElement fxButtonElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div[2]/div/div/div[2]/div/div/div/div/div/button")));
	    fxButtonElement.click();
	    
	    // Start the knet payment
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div[2]/div/div/div[7]/div/div/div/div/button")).click();
	    
	    
	    // Wait for KNET to be loaded
	    driver.switchTo().parentFrame();
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/form/div[1]/div/div[2]/div[3]/div[1]/div[2]/select")));
	    
	    (new Select(driver.findElement(By.xpath("/html/body/form/div[1]/div/div[2]/div[3]/div[1]/div[2]/select")))).selectByValue("201825717889145|Knet Test Card [KNET1]|0.000|0.00|0.000");
	    wait.until(ExpectedConditions.elementToBeClickable(By.id("debitNumber")));
	    driver.findElement(By.id("debitNumber")).sendKeys("0000000001");
	    
	    (new Select(driver.findElement(By.xpath("/html/body/form/div[1]/div/div[2]/div[3]/div[3]/div[2]/select[1]")))).selectByValue("9");
	    (new Select(driver.findElement(By.xpath("/html/body/form/div[1]/div/div[2]/div[3]/div[3]/div[2]/select[2]")))).selectByValue("2025");
	    
	    driver.findElement(By.id("cardPin")).sendKeys("1234");
	    driver.findElement(By.id("proceed")).click();
	    
	    wait.until(ExpectedConditions.elementToBeClickable(By.id("proceedConfirm")));
	    driver.findElement(By.id("proceedConfirm")).click();
	    
	    // Fetch the charge id
	    wait.until(ExpectedConditions.urlContains("demo.staging"));
	    List<LogEntry> entries = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
	     for (LogEntry entry : entries) {
	    	 try {
	    		 ObjectMapper mapper = new ObjectMapper();
	    		 JsonNode rootNode = mapper.readTree(entry.getMessage());
	    		 if(rootNode.has("message") &&
	    				 rootNode.findValue("message").has("params") &&
	    				 rootNode.findValue("message").findValue("params").has("documentURL")) {
	    			 String url = rootNode.findValue("message").findValue("params").findValue("documentURL").asText();
	    			 if(url.contains("tap_id")) {
	    				 String chargeID = url.split("tap_id=")[1];
	    				 String chargeStatus = getChargeStatus(chargeID);
	    				 System.out.println(((chargeStatus.equalsIgnoreCase("captured")) ? "✅" : "⚠️") + "KNET - SAR - POPUP - "+chargeID+" - "+getChargeStatus(chargeID));
	    				 break;
	    			 }
	    		 }
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}  
	     }
	    
	    driver.close();
	}
	
	public static void paypalWithSAR() {
		ChromeOptions options = new ChromeOptions();
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable( LogType.PERFORMANCE, Level.ALL );
		options.setCapability( "goog:loggingPrefs", logPrefs );
		WebDriver driver = new ChromeDriver(options);
		
		driver.get("https://demo.staging.tap.company/v2/sdk/checkout");
	    driver.manage().window().setSize(new Dimension(1200, 859));

	    // Start the checkout
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[3]/button")).click();

	    // Wait for the checkout iFrame to be loaded
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(100000));
	    WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div[2]/div/iframe")));
	    driver.switchTo().frame("tap-checkout-sdk-iframe");
	    
	    // Wait for the paypal chip to be rendered
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div[2]/div/div/div[1]/div/div/div/div[1]/div/div/div/h6[1]")));
	    List<WebElement> chipsElements = driver.findElements(By.xpath("//img[@class='MuiBox-root css-qmsygv']"));
	    for (WebElement webElement : chipsElements) {
	    	String imgSrc = webElement.getAttribute("src");
			if(!imgSrc.isEmpty() && imgSrc.contains("paypal.svg")) {
				wait.until(ExpectedConditions.elementToBeClickable(webElement));
				webElement.click();
				break;
			}
		}
	    
	    // Wait for the FX change to USD widget is visible
	    WebElement fxButtonElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div[2]/div/div/div[2]/div/div/div/div/div/button")));
	    fxButtonElement.click();
	    
	    // Start the paypal payment
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div[2]/div/div/div[7]/div/div/div/div/button")).click();
	    
	    
	    // Wait for paypal to be loaded
	    driver.switchTo().parentFrame();
	    // Enter paypal email
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/section[1]/div[1]/div[2]/form/div[3]/div[1]/div[2]/div[1]/input")));
	    driver.findElement(By.xpath("/html/body/div[1]/section[1]/div[1]/div[2]/form/div[3]/div[1]/div[2]/div[1]/input")).sendKeys("sb-vas0o14312176@personal.example.com");
	    driver.findElement(By.xpath("/html/body/div[1]/section[1]/div[1]/div[2]/form/div[3]/div[2]/button")).click();
	    
	    // Enter paypal password
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/section[1]/div[1]/div[2]/form/div[4]/div[1]/div/div/div[1]/input")));
	    driver.findElement(By.xpath("/html/body/div[1]/section[1]/div[1]/div[2]/form/div[4]/div[1]/div/div/div[1]/input")).sendKeys("talhi112233");
	    driver.findElement(By.xpath("/html/body/div[1]/section[1]/div[1]/div[2]/form/div[4]/div[3]/button")).click();
	    
	    // Choose paypal test card
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[2]/div/div/div/main/div[2]/section/div[4]/div[1]/div/div[1]/div[1]/div/label/div")));
	    driver.findElement(By.xpath("/html/body/div[2]/div/div/div/main/div[2]/section/div[4]/div[1]/div/div[1]/div[1]/div/label/div")).click();
	    driver.findElement(By.xpath("/html/body/div[2]/div/div/div/main/div[3]/div[2]/button")).click();
	    
	    // Fetch the charge id
	    wait.until(ExpectedConditions.urlContains("demo.staging"));
	    List<LogEntry> entries = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
	     for (LogEntry entry : entries) {
	    	 try {
	    		 ObjectMapper mapper = new ObjectMapper();
	    		 JsonNode rootNode = mapper.readTree(entry.getMessage());
	    		 if(rootNode.has("message") &&
	    				 rootNode.findValue("message").has("params") &&
	    				 rootNode.findValue("message").findValue("params").has("documentURL")) {
	    			 String url = rootNode.findValue("message").findValue("params").findValue("documentURL").asText();
	    			 if(url.contains("tap_id")) {
	    				 String chargeID = url.split("tap_id=")[1];
	    				 String chargeStatus = getChargeStatus(chargeID);
	    				 System.out.println(((chargeStatus.equalsIgnoreCase("captured")) ? "✅" : "⚠️") + "PAYPAL - SAR - POPUP - "+chargeID+" - "+getChargeStatus(chargeID));
	    				 break;
	    			 }
	    		 }
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}  
	     }
	    
	    driver.close();
	}
	
	public static void paypalWithUSD() {
		ChromeOptions options = new ChromeOptions();
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable( LogType.PERFORMANCE, Level.ALL );
		options.setCapability( "goog:loggingPrefs", logPrefs );
		WebDriver driver = new ChromeDriver(options);
		
		driver.get("https://demo.staging.tap.company/v2/sdk/checkout");
	    driver.manage().window().setSize(new Dimension(1200, 859));

	    // Select the currency
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[2]/div/div[6]/div/div")).click();
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul/li[8]")).click();
	    
	    // Start the checkout
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[3]/button")).click();

	    // Wait for the checkout iFrame to be loaded
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(100000));
	    WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div[2]/div/iframe")));
	    driver.switchTo().frame("tap-checkout-sdk-iframe");
	    
	    // Wait for the paypal chip to be rendered
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div[2]/div/div/div[1]/div/div/div/div[1]/div/div/div/h6[1]")));
	    List<WebElement> chipsElements = driver.findElements(By.xpath("//img[@class='MuiBox-root css-qmsygv']"));
	    for (WebElement webElement : chipsElements) {
	    	String imgSrc = webElement.getAttribute("src");
			if(!imgSrc.isEmpty() && imgSrc.contains("paypal.svg")) {
				wait.until(ExpectedConditions.elementToBeClickable(webElement));
				webElement.click();
				break;
			}
		}
	    //WebElement paypalElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[3]/div/div/div[2]/div/div/div[1]/div/div/div/div[2]/div[1]/div/div/ul/button[3]")));
	    //paypalElement.click();
	    
	    // Start the paypal payment
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div[2]/div/div/div[7]/div/div/div/div/button")).click();
	    
	    
	    // Wait for paypal to be loaded
	    driver.switchTo().parentFrame();
	    // Enter paypal email
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/section[1]/div[1]/div[2]/form/div[3]/div[1]/div[2]/div[1]/input")));
	    driver.findElement(By.xpath("/html/body/div[1]/section[1]/div[1]/div[2]/form/div[3]/div[1]/div[2]/div[1]/input")).sendKeys("sb-vas0o14312176@personal.example.com");
	    driver.findElement(By.xpath("/html/body/div[1]/section[1]/div[1]/div[2]/form/div[3]/div[2]/button")).click();
	    
	    // Enter paypal password
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/section[1]/div[1]/div[2]/form/div[4]/div[1]/div/div/div[1]/input")));
	    driver.findElement(By.xpath("/html/body/div[1]/section[1]/div[1]/div[2]/form/div[4]/div[1]/div/div/div[1]/input")).sendKeys("talhi112233");
	    driver.findElement(By.xpath("/html/body/div[1]/section[1]/div[1]/div[2]/form/div[4]/div[3]/button")).click();
	    
	    // Choose paypal test card
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[2]/div/div/div/main/div[2]/section/div[4]/div[1]/div/div[1]/div[1]/div/label/div")));
	    driver.findElement(By.xpath("/html/body/div[2]/div/div/div/main/div[2]/section/div[4]/div[1]/div/div[1]/div[1]/div/label/div")).click();
	    driver.findElement(By.xpath("/html/body/div[2]/div/div/div/main/div[3]/div[2]/button")).click();
	    
	    // Fetch the charge id
	    wait.until(ExpectedConditions.urlContains("demo.staging"));
	    List<LogEntry> entries = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
	     for (LogEntry entry : entries) {
	    	 try {
	    		 ObjectMapper mapper = new ObjectMapper();
	    		 JsonNode rootNode = mapper.readTree(entry.getMessage());
	    		 if(rootNode.has("message") &&
	    				 rootNode.findValue("message").has("params") &&
	    				 rootNode.findValue("message").findValue("params").has("documentURL")) {
	    			 String url = rootNode.findValue("message").findValue("params").findValue("documentURL").asText();
	    			 if(url.contains("tap_id")) {
	    				 String chargeID = url.split("tap_id=")[1];
	    				 String chargeStatus = getChargeStatus(chargeID);
	    				 System.out.println(((chargeStatus.equalsIgnoreCase("captured")) ? "✅" : "⚠️") + "PAYPAL - USD - POPUP - "+chargeID+" - "+getChargeStatus(chargeID));
	    				 break;
	    			 }
	    		 }
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}  
	     }
	    
	    driver.close();
	}
	
	public static void benefitWithSAR() {
		ChromeOptions options = new ChromeOptions();
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable( LogType.PERFORMANCE, Level.ALL );
		options.setCapability( "goog:loggingPrefs", logPrefs );
		WebDriver driver = new ChromeDriver(options);
		
		driver.get("https://demo.staging.tap.company/v2/sdk/checkout");
	    driver.manage().window().setSize(new Dimension(1200, 859));
	    // Start the checkout
	    
	    
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[3]/button")).click();

	    // Wait for the checkout iFrame to be loaded
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(100000));
	    WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div[2]/div/iframe")));
	    driver.switchTo().frame("tap-checkout-sdk-iframe");
	    
	    // Wait for the benefit chip to be rendered
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div[2]/div/div/div[1]/div/div/div/div[1]/div/div/div/h6[1]")));
	    List<WebElement> chipsElements = driver.findElements(By.xpath("//img[@class='MuiBox-root css-qmsygv']"));
	    for (WebElement webElement : chipsElements) {
	    	String imgSrc = webElement.getAttribute("src");
			if(!imgSrc.isEmpty() && imgSrc.contains("benefit.svg")) {
				wait.until(ExpectedConditions.elementToBeClickable(webElement));
				webElement.click();
				break;
			}
		}
	    
	    // Wait for the FX change to BHD widget is visible
	    WebElement fxButtonElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("html/body/div[2]/div[3]/div/div/section/div[3]/div[3]/div/div/div[2]/div/div/div[2]/div/div/div/div/div/button")));
	    fxButtonElement.click();
	    
	    // Start the benefit payment
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div[2]/div/div/div[7]/div/div/div/div/button")).click();
	    
	    
	    // Wait for benefit to be loaded
	    driver.switchTo().parentFrame();
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("headerDiv")));
	    System.out.println("HERE");
	    // Card number
	    JavascriptExecutor jse = (JavascriptExecutor)driver;
	    jse.executeScript("document.getElementById('debitCardNumber').setAttribute('value', '4600410123456789')");
	    //driver.findElement(By.id("debitCardNumber")).sendKeys("");
	    System.out.println("HERE3");
	    // Card expiry
	    (new Select(driver.findElement(By.id("debitMonthSelect")))).selectByValue("12");
	    (new Select(driver.findElement(By.id("debitYearSelect")))).selectByValue("2027");
	    System.out.println("HERE2");
	    // Card name
	    driver.findElement(By.id("debitCardholderName")).sendKeys("OSAMA AHMED HELMY");
	    jse.executeScript("document.getElementById('debitCardNumber').focus({ focusVisible: true });");
	    jse.executeScript("document.getElementById('debitCardNumber').blur();");
	    // Card pin
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cardPin")));
	    jse.executeScript("document.getElementById('cardPin').setAttribute('value', '1234')");	    
	    // Pay
	    driver.findElement(By.id("proceed")).click();
	    
	    // Fetch the charge id
	    wait.until(ExpectedConditions.urlContains("demo.staging"));
	    List<LogEntry> entries = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
	     for (LogEntry entry : entries) {
	    	 try {
	    		 ObjectMapper mapper = new ObjectMapper();
	    		 JsonNode rootNode = mapper.readTree(entry.getMessage());
	    		 if(rootNode.has("message") &&
	    				 rootNode.findValue("message").has("params") &&
	    				 rootNode.findValue("message").findValue("params").has("documentURL")) {
	    			 String url = rootNode.findValue("message").findValue("params").findValue("documentURL").asText();
	    			 if(url.contains("tap_id")) {
	    				 String chargeID = url.split("tap_id=")[1];
	    				 String chargeStatus = getChargeStatus(chargeID);
	    				 System.out.println(((chargeStatus.equalsIgnoreCase("captured")) ? "✅" : "⚠️") + "BENEFIT - SAR - POPUP - "+chargeID+" - "+getChargeStatus(chargeID));
	    				 break;
	    			 }
	    		 }
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}  
	     }
	    
	    driver.close();
	}
	
	public static void benefitWithBHD() {
		ChromeOptions options = new ChromeOptions();
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable( LogType.PERFORMANCE, Level.ALL );
		options.setCapability( "goog:loggingPrefs", logPrefs );
		WebDriver driver = new ChromeDriver(options);
		
		driver.get("https://demo.staging.tap.company/v2/sdk/checkout");
	    driver.manage().window().setSize(new Dimension(1200, 859));
	    
	    // Select the currency
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[2]/div/div[6]/div/div")).click();
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul/li[2]")).click();

	    // Start the checkout
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[3]/button")).click();

	    // Wait for the checkout iFrame to be loaded
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(100000));
	    WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div[2]/div/iframe")));
	    driver.switchTo().frame("tap-checkout-sdk-iframe");
	    
	    // Wait for the benefit chip to be rendered
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div[2]/div/div/div[1]/div/div/div/div[1]/div/div/div/h6[1]")));
	    List<WebElement> chipsElements = driver.findElements(By.xpath("//img[@class='MuiBox-root css-qmsygv']"));
	    for (WebElement webElement : chipsElements) {
	    	String imgSrc = webElement.getAttribute("src");
			if(!imgSrc.isEmpty() && imgSrc.contains("benefit.svg")) {
				wait.until(ExpectedConditions.elementToBeClickable(webElement));
				webElement.click();
				break;
			}
		}
	    
	    // Start the benefit payment 
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div[2]/div/div/div[7]/div/div/div/div/button")).click();
	    
	    // Wait for benefit to be loaded
	    driver.switchTo().parentFrame();
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("headerDiv")));
	    System.out.println("HERE");
	    // Card number
	    JavascriptExecutor jse = (JavascriptExecutor)driver;
	    jse.executeScript("document.getElementById('debitCardNumber').setAttribute('value', '4600410123456789')");
	    //driver.findElement(By.id("debitCardNumber")).sendKeys("");
	    System.out.println("HERE3");
	    // Card expiry
	    (new Select(driver.findElement(By.id("debitMonthSelect")))).selectByValue("12");
	    (new Select(driver.findElement(By.id("debitYearSelect")))).selectByValue("2027");
	    System.out.println("HERE2");
	    // Card name
	    driver.findElement(By.id("debitCardholderName")).sendKeys("OSAMA AHMED HELMY");
	    jse.executeScript("document.getElementById('debitCardNumber').focus({ focusVisible: true });");
	    jse.executeScript("document.getElementById('debitCardNumber').blur();");
	    // Card pin
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cardPin")));
	    jse.executeScript("document.getElementById('cardPin').setAttribute('value', '1234')");	    
	    // Pay
	    driver.findElement(By.id("proceed")).click();
	    
	    // Fetch the charge id
	    wait.until(ExpectedConditions.urlContains("demo.staging"));
	    List<LogEntry> entries = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
	     for (LogEntry entry : entries) {
	    	 try {
	    		 ObjectMapper mapper = new ObjectMapper();
	    		 JsonNode rootNode = mapper.readTree(entry.getMessage());
	    		 if(rootNode.has("message") &&
	    				 rootNode.findValue("message").has("params") &&
	    				 rootNode.findValue("message").findValue("params").has("documentURL")) {
	    			 String url = rootNode.findValue("message").findValue("params").findValue("documentURL").asText();
	    			 if(url.contains("tap_id")) {
	    				 String chargeID = url.split("tap_id=")[1];
	    				 String chargeStatus = getChargeStatus(chargeID);
	    				 System.out.println(((chargeStatus.equalsIgnoreCase("captured")) ? "✅" : "⚠️") + "BENEFIT - BHD - POPUP - "+chargeID+" - "+getChargeStatus(chargeID));
	    				 break;
	    			 }
	    		 }
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}  
	     }
	    
	    driver.close();
	}
	
	public static void careemPayWithSAR() {
		ChromeOptions options = new ChromeOptions();
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable( LogType.PERFORMANCE, Level.ALL );
		options.setCapability( "goog:loggingPrefs", logPrefs );
		WebDriver driver = new ChromeDriver(options);
		
		driver.get("https://demo.staging.tap.company/v2/sdk/checkout");
	    driver.manage().window().setSize(new Dimension(1200, 859));
	    // Start the checkout
	    
	    
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[3]/button")).click();

	    // Wait for the checkout iFrame to be loaded
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(100000));
	    WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div[2]/div/iframe")));
	    driver.switchTo().frame("tap-checkout-sdk-iframe");
	    
	    // Wait for the careempay chip to be rendered
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div[2]/div/div/div[1]/div/div/div/div[1]/div/div/div/h6[1]")));
	    List<WebElement> chipsElements = driver.findElements(By.xpath("//img[@class='MuiBox-root css-qmsygv']"));
	    for (WebElement webElement : chipsElements) {
	    	String imgSrc = webElement.getAttribute("src");
			if(!imgSrc.isEmpty() && imgSrc.contains("careempay.svg")) {
				wait.until(ExpectedConditions.elementToBeClickable(webElement));
				webElement.click();
				break;
			}
		}
	    
	    // Wait for the FX change to AED widget is visible
	    WebElement fxButtonElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("html/body/div[2]/div[3]/div/div/section/div[3]/div[3]/div/div/div[2]/div/div/div[2]/div/div/div/div/div/button")));
	    fxButtonElement.click();
	    
	    // Start the careempay payment
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[3]/div/div/div[2]/div/div/div[6]/div/div/div/div/section/div[1]/button")).click();
	    
	    
	    // Wait for careempay to be loaded
	    driver.switchTo().parentFrame();
	    // Enter the phone number
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/main/form/div/div/div[1]/input")));
	    //driver.findElement(By.xpath("/html/body/div[1]/main/form/div/div/div[1]/input")).sendKeys("585917625");
	    WebElement careemNumberElement = driver.findElement(By.xpath("/html/body/div[1]/main/form/div/div/div[1]/input")); // you can use any locator

	    try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    JavascriptExecutor jse = (JavascriptExecutor)driver;
	    careemNumberElement.sendKeys("0585917625");
	    jse.executeScript("arguments[0].value='0585917625';", careemNumberElement);
	    wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"main-panel\"]/form/div/div/div[1]/input[2]")));
	    WebElement careemHiddenNumberElement = driver.findElement(By.xpath("//*[@id=\"main-panel\"]/form/div/div/div[1]/input[2]")); // you can use any locator
	    jse.executeScript("arguments[0].value='+971585917625';", careemHiddenNumberElement);
	    driver.findElement(By.xpath("/html/body/div[1]/main/form/footer/button")).click();
	    // Enter the otp
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/main/form/div/div/div[1]/div[2]/div/div/input[1]")));
	    try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    driver.findElement(By.xpath("/html/body/div[1]/main/form/div/div/div[1]/div[2]/div/div/input[1]")).sendKeys("1");
	    driver.findElement(By.xpath("/html/body/div[1]/main/form/div/div/div[1]/div[2]/div/div/input[2]")).sendKeys("2");
	    driver.findElement(By.xpath("/html/body/div[1]/main/form/div/div/div[1]/div[2]/div/div/input[3]")).sendKeys("3");
	    driver.findElement(By.xpath("/html/body/div[1]/main/form/div/div/div[1]/div[2]/div/div/input[4]")).sendKeys("4");
	    
	    // Enter the password
	    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/main/form/div/div/div[1]/div[2]/button")));
	    try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    driver.findElement(By.xpath("/html/body/div[1]/main/form/div/div/div[1]/div[2]/button")).click();
	    
	    // Enter the customer details
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/main/form/div/div/fieldset[1]/div[2]/div[1]/div[1]/div/input")));
	    driver.findElement(By.xpath("/html/body/div/main/form/div/div/fieldset[1]/div[2]/div[1]/div[1]/div/input")).sendKeys("Osama Ahmed");
	    driver.findElement(By.xpath("/html/body/div/main/form/div/div/fieldset[1]/div[2]/div[1]/div[2]/div/input")).sendKeys("o.rabie@tap.company");
	    driver.findElement(By.xpath("/html/body/div/main/form/div/div/fieldset[1]/div[2]/div[3]/div/div[1]/input")).sendKeys("amar15406");
	    try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    // Enter the card details
	    driver.switchTo().frame(0);
	    driver.findElement(By.xpath("/html/body/div/input[1]")).sendKeys("4508750015741019");
	    driver.switchTo().parentFrame();
	    driver.switchTo().frame(1);
	    //jse.executeScript("arguments[0].value='01 / 2039';", driver.findElement(By.xpath("/html/body/div/input[1]")));
	    driver.findElement(By.xpath("/html/body/div/input[1]")).sendKeys("0");
	    driver.findElement(By.xpath("/html/body/div/input[1]")).sendKeys("1");
	    try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    driver.findElement(By.xpath("/html/body/div/input[1]")).sendKeys("2039");
	    driver.switchTo().parentFrame();
	    driver.switchTo().frame(2);
	    driver.findElement(By.xpath("/html/body/div/input[1]")).sendKeys("100");
	    
	    driver.switchTo().parentFrame();
	    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div/main/form/footer/button")));
	    try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    driver.findElement(By.xpath("/html/body/div/main/form/footer/button")).click();

	    // Fetch the charge id
	    wait.until(ExpectedConditions.urlContains("demo.staging"));
	    List<LogEntry> entries = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
	     for (LogEntry entry : entries) {
	    	 try {
	    		 ObjectMapper mapper = new ObjectMapper();
	    		 JsonNode rootNode = mapper.readTree(entry.getMessage());
	    		 if(rootNode.has("message") &&
	    				 rootNode.findValue("message").has("params") &&
	    				 rootNode.findValue("message").findValue("params").has("documentURL")) {
	    			 String url = rootNode.findValue("message").findValue("params").findValue("documentURL").asText();
	    			 if(url.contains("tap_id")) {
	    				 String chargeID = url.split("tap_id=")[1];
	    				 String chargeStatus = getChargeStatus(chargeID);
	    				 System.out.println(((chargeStatus.equalsIgnoreCase("captured")) ? "✅" : "⚠️") + "BENEFIT - SAR - POPUP - "+chargeID+" - "+getChargeStatus(chargeID));
	    				 break;
	    			 }
	    		 }
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}  
	     }
	    
	    driver.close();
	}
	
	public static void AmexWithKWD() {
		ChromeOptions options = new ChromeOptions();
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable( LogType.PERFORMANCE, Level.ALL );
		options.setCapability( "goog:loggingPrefs", logPrefs );
		WebDriver driver = new ChromeDriver(options);
		
		driver.get("https://demo.staging.tap.company/v2/sdk/checkout");
	    driver.manage().window().setSize(new Dimension(1200, 859));
	    // select currency
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[2]/div/div[6]/div/div/div")).click();
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul/li[1]")).click();
	    // Page mode
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[1]/div[5]/button/span[1]/span[1]")).click();
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[2]/div/div[3]/div/div/div")).click();
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul/li[2]")).click();

	    // Start the checkout
	    
	    
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[3]/button")).click();

	    // Wait for the card iFrame to be loaded
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(100000));
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tap-card-iframe")));
	    driver.switchTo().frame("tap-card-iframe");
	    
	    // Enter the card details
	    driver.findElement(By.id("card_input_mini")).sendKeys("345678901234564");
	    driver.findElement(By.id("date_input")).sendKeys("01/39");
	    driver.findElement(By.id("cvv_input")).sendKeys("1000");
	    wait.until(ExpectedConditions.elementToBeClickable(By.id("cardHolderName_input")));
	    driver.findElement(By.id("cardHolderName_input")).sendKeys("OSAMA AHMED HELMY");
	    
	    // Click on change to SAR from the Card FX widget
	    driver.switchTo().parentFrame();
	    
	    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div[2]/div/div/div[6]/div/div/div/div/div/button")));
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div[2]/div/div/div[6]/div/div/div/div/div/button")).click();
	    // Start the card payment
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div[2]/div/div/div[7]/div/div/div/div/section/button")).click();
	    
	    // Wait for 3ds iframe
	    driver.switchTo().frame("tap-card-iframe");
	    driver.switchTo().frame("tap-card-iframe-authentication");
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("challengeFrame")));
	    driver.switchTo().frame("challengeFrame");
	    
	    WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable
                (By.id("acssubmit")));
		JavascriptExecutor js = (JavascriptExecutor)driver;
		js.executeScript("arguments[0].click();", submitButton);
		
		// Fetch the charge id
	    wait.until(ExpectedConditions.urlContains("demo.staging"));
	    List<LogEntry> entries = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
	     for (LogEntry entry : entries) {
	    	 
	    	 try {
	    		 ObjectMapper mapper = new ObjectMapper();
	    		 JsonNode rootNode = mapper.readTree(entry.getMessage());
	    		 if(rootNode.has("message") &&
	    				 rootNode.findValue("message").has("params") &&
	    				 rootNode.findValue("message").findValue("params").has("documentURL")) {
	    			 String url = rootNode.findValue("message").findValue("params").findValue("documentURL").asText();
	    			 String chargeID = "";
	    			 if(url.contains("tap_id") || url.contains("checkout/charge/chg_")) {
	    				 if(url.contains("tap_id")) {
	    					 chargeID = url.split("tap_id=")[1];
	    				 }else {
	    					 chargeID = url.split("checkout/charge/")[1];
	    				 }
	    				 String chargeStatus = getChargeStatus(chargeID);
	    				 System.out.println(((chargeStatus.equalsIgnoreCase("captured")) ? "✅" : "⚠️") + "AMEX - KWD  "+chargeID+" - "+getChargeStatus(chargeID));
	    				 break;
	    			 }
	    		 }
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}  
	     }
	    
	    driver.close();
	}
	
	public static void AmexWithUSD() {
		ChromeOptions options = new ChromeOptions();
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable( LogType.PERFORMANCE, Level.ALL );
		options.setCapability( "goog:loggingPrefs", logPrefs );
		WebDriver driver = new ChromeDriver(options);
		
		driver.get("https://demo.staging.tap.company/v2/sdk/checkout");
	    driver.manage().window().setSize(new Dimension(1200, 859));
	    // select currency
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[2]/div/div[6]/div/div/div")).click();
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul/li[8]")).click();
	    // Page mode
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[1]/div[5]/button/span[1]/span[1]")).click();
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[2]/div/div[3]/div/div/div")).click();
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul/li[2]")).click();

	    // Start the checkout
	    
	    
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[3]/button")).click();

	    // Wait for the card iFrame to be loaded
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(100000));
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tap-card-iframe")));
	    driver.switchTo().frame("tap-card-iframe");
	    
	    // Enter the card details
	    driver.findElement(By.id("card_input_mini")).sendKeys("345678901234564");
	    driver.findElement(By.id("date_input")).sendKeys("01/39");
	    driver.findElement(By.id("cvv_input")).sendKeys("1000");
	    wait.until(ExpectedConditions.elementToBeClickable(By.id("cardHolderName_input")));
	    driver.findElement(By.id("cardHolderName_input")).sendKeys("OSAMA AHMED HELMY");
	    
	    // Click on change to SAR from the Card FX widget
	    driver.switchTo().parentFrame();
	    
	    // Start the card payment
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div[2]/div/div/div[7]/div/div/div/div/section/button")).click();
	    
	    // Wait for 3ds iframe
	    driver.switchTo().frame("tap-card-iframe");
	    driver.switchTo().frame("tap-card-iframe-authentication");
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("challengeFrame")));
	    driver.switchTo().frame("challengeFrame");
	    
	    WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable
                (By.id("acssubmit")));
		JavascriptExecutor js = (JavascriptExecutor)driver;
		js.executeScript("arguments[0].click();", submitButton);
		
		// Fetch the charge id
	    wait.until(ExpectedConditions.urlContains("demo.staging"));
	    List<LogEntry> entries = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
	     for (LogEntry entry : entries) {
	    	 
	    	 try {
	    		 ObjectMapper mapper = new ObjectMapper();
	    		 JsonNode rootNode = mapper.readTree(entry.getMessage());
	    		 if(rootNode.has("message") &&
	    				 rootNode.findValue("message").has("params") &&
	    				 rootNode.findValue("message").findValue("params").has("documentURL")) {
	    			 String url = rootNode.findValue("message").findValue("params").findValue("documentURL").asText();
	    			 String chargeID = "";
	    			 if(url.contains("tap_id") || url.contains("checkout/charge/chg_")) {
	    				 if(url.contains("tap_id")) {
	    					 chargeID = url.split("tap_id=")[1];
	    				 }else {
	    					 chargeID = url.split("checkout/charge/")[1];
	    				 }
	    				 String chargeStatus = getChargeStatus(chargeID);
	    				 System.out.println(((chargeStatus.equalsIgnoreCase("captured")) ? "✅" : "⚠️") + "AMEX - USD  "+chargeID+" - "+getChargeStatus(chargeID));
	    				 break;
	    			 }
	    		 }
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}  
	     }
	    
	    driver.close();
	}
	
	public static void MCWithSAR() {
		ChromeOptions options = new ChromeOptions();
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable( LogType.PERFORMANCE, Level.ALL );
		options.setCapability( "goog:loggingPrefs", logPrefs );
		WebDriver driver = new ChromeDriver(options);
		
		driver.get("https://demo.staging.tap.company/v2/sdk/checkout");
	    driver.manage().window().setSize(new Dimension(1200, 859));
	    // Page mode
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[1]/div[5]/button/span[1]/span[1]")).click();
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[2]/div/div[3]/div/div/div")).click();
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul/li[2]")).click();

	    // Start the checkout
	    
	    
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[3]/button")).click();

	    // Wait for the card iFrame to be loaded
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(100000));
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tap-card-iframe")));
	    driver.switchTo().frame("tap-card-iframe");
	    
	    // Enter the card details
	    driver.findElement(By.id("card_input_mini")).sendKeys("5123450000000008");
	    driver.findElement(By.id("date_input")).sendKeys("01/29");
	    driver.findElement(By.id("cvv_input")).sendKeys("100");
	    wait.until(ExpectedConditions.elementToBeClickable(By.id("cardHolderName_input")));
	    driver.findElement(By.id("cardHolderName_input")).sendKeys("OSAMA AHMED HELMY");
	    
	    
	    driver.switchTo().parentFrame();
	    // Start the card payment
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div[2]/div/div/div[7]/div/div/div/div/section/button")).click();
	    
	    // Wait for 3ds iframe
	    driver.switchTo().frame("tap-card-iframe");
	    driver.switchTo().frame("tap-card-iframe-authentication");
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("challengeFrame")));
	    driver.switchTo().frame("challengeFrame");
	    
	    WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable
                (By.id("acssubmit")));
		JavascriptExecutor js = (JavascriptExecutor)driver;
		js.executeScript("arguments[0].click();", submitButton);
		
		// Fetch the charge id
	    wait.until(ExpectedConditions.urlContains("demo.staging"));
	    List<LogEntry> entries = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
	     for (LogEntry entry : entries) {
	    	 
	    	 try {
	    		 ObjectMapper mapper = new ObjectMapper();
	    		 JsonNode rootNode = mapper.readTree(entry.getMessage());
	    		 if(rootNode.has("message") &&
	    				 rootNode.findValue("message").has("params") &&
	    				 rootNode.findValue("message").findValue("params").has("documentURL")) {
	    			 String url = rootNode.findValue("message").findValue("params").findValue("documentURL").asText();
	    			 String chargeID = "";
	    			 if(url.contains("tap_id") || url.contains("checkout/charge/chg_")) {
	    				 if(url.contains("tap_id")) {
	    					 chargeID = url.split("tap_id=")[1];
	    				 }else {
	    					 chargeID = url.split("checkout/charge/")[1];
	    				 }
	    				 String chargeStatus = getChargeStatus(chargeID);
	    				 System.out.println(((chargeStatus.equalsIgnoreCase("captured")) ? "✅" : "⚠️") + "MC - SAR  "+chargeID+" - "+getChargeStatus(chargeID));
	    				 break;
	    			 }
	    		 }
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}  
	     }
	    
	    driver.close();
	}
	
	public static void VISAWithUSD() {
		ChromeOptions options = new ChromeOptions();
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable( LogType.PERFORMANCE, Level.ALL );
		options.setCapability( "goog:loggingPrefs", logPrefs );
		WebDriver driver = new ChromeDriver(options);
		
		driver.get("https://demo.staging.tap.company/v2/sdk/checkout");
	    driver.manage().window().setSize(new Dimension(1200, 859));
	    // select currency
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[2]/div/div[6]/div/div/div")).click();
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul/li[8]")).click();
	    // Page mode
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[1]/div[5]/button/span[1]/span[1]")).click();
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[2]/div/div[3]/div/div/div")).click();
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul/li[2]")).click();

	    // Start the checkout
	    
	    
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[3]/button")).click();

	    // Wait for the card iFrame to be loaded
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(100000));
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tap-card-iframe")));
	    driver.switchTo().frame("tap-card-iframe");
	    
	    // Enter the card details
	    driver.findElement(By.id("card_input_mini")).sendKeys("4508750015741019");
	    driver.findElement(By.id("date_input")).sendKeys("01/39");
	    driver.findElement(By.id("cvv_input")).sendKeys("100");
	    wait.until(ExpectedConditions.elementToBeClickable(By.id("cardHolderName_input")));
	    driver.findElement(By.id("cardHolderName_input")).sendKeys("OSAMA AHMED HELMY");
	    
	    
	    driver.switchTo().parentFrame();
	    // Start the card payment
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div[2]/div/div/div[7]/div/div/div/div/section/button")).click();
	    
	    // Wait for 3ds iframe
	    driver.switchTo().frame("tap-card-iframe");
	    driver.switchTo().frame("tap-card-iframe-authentication");
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("challengeFrame")));
	    driver.switchTo().frame("challengeFrame");
	    
	    WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable
                (By.id("acssubmit")));
		JavascriptExecutor js = (JavascriptExecutor)driver;
		js.executeScript("arguments[0].click();", submitButton);
		
		// Fetch the charge id
	    wait.until(ExpectedConditions.urlContains("demo.staging"));
	    List<LogEntry> entries = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
	     for (LogEntry entry : entries) {
	    	 
	    	 try {
	    		 ObjectMapper mapper = new ObjectMapper();
	    		 JsonNode rootNode = mapper.readTree(entry.getMessage());
	    		 if(rootNode.has("message") &&
	    				 rootNode.findValue("message").has("params") &&
	    				 rootNode.findValue("message").findValue("params").has("documentURL")) {
	    			 String url = rootNode.findValue("message").findValue("params").findValue("documentURL").asText();
	    			 String chargeID = "";
	    			 if(url.contains("tap_id") || url.contains("checkout/charge/chg_")) {
	    				 if(url.contains("tap_id")) {
	    					 chargeID = url.split("tap_id=")[1];
	    				 }else {
	    					 chargeID = url.split("checkout/charge/")[1];
	    				 }
	    				 String chargeStatus = getChargeStatus(chargeID);
	    				 System.out.println(((chargeStatus.equalsIgnoreCase("captured")) ? "✅" : "⚠️") + "VISA - USD  "+chargeID+" - "+getChargeStatus(chargeID));
	    				 break;
	    			 }
	    		 }
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}  
	     }
	    
	    driver.close();
	}
	
	public static void madaWithSAR() {
		ChromeOptions options = new ChromeOptions();
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable( LogType.PERFORMANCE, Level.ALL );
		options.setCapability( "goog:loggingPrefs", logPrefs );
		WebDriver driver = new ChromeDriver(options);
		
		driver.get("https://demo.staging.tap.company/v2/sdk/checkout");
	    driver.manage().window().setSize(new Dimension(1200, 859));
	    // Page mode
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[1]/div[5]/button/span[1]/span[1]")).click();
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[2]/div/div[3]/div/div/div")).click();
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul/li[2]")).click();
	    
	    // Start the checkout
	    
	    
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[3]/button")).click();

	    // Wait for the card iFrame to be loaded
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(100000));
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tap-card-iframe")));
	    driver.switchTo().frame("tap-card-iframe");
	    
	    // Enter the card details
	    driver.findElement(By.id("card_input_mini")).sendKeys("4464040000000007");
	    driver.findElement(By.id("date_input")).sendKeys("01/29");
	    driver.findElement(By.id("cvv_input")).sendKeys("100");
	    wait.until(ExpectedConditions.elementToBeClickable(By.id("cardHolderName_input")));
	    driver.findElement(By.id("cardHolderName_input")).sendKeys("OSAMA AHMED HELMY");
	    
	    
	    driver.switchTo().parentFrame();
	    // Start the card payment
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div[2]/div/div/div[7]/div/div/div/div/section/button")).click();
	    
	    // Fetch the charge id
	    wait.until(ExpectedConditions.urlContains("demo.staging"));
	    List<LogEntry> entries = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
	     for (LogEntry entry : entries) {
	    	 
	    	 try {
	    		 ObjectMapper mapper = new ObjectMapper();
	    		 JsonNode rootNode = mapper.readTree(entry.getMessage());
	    		 if(rootNode.has("message") &&
	    				 rootNode.findValue("message").has("params") &&
	    				 rootNode.findValue("message").findValue("params").has("documentURL")) {
	    			 String url = rootNode.findValue("message").findValue("params").findValue("documentURL").asText();
	    			 String chargeID = "";
	    			 if(url.contains("tap_id") || url.contains("checkout/charge/chg_")) {
	    				 if(url.contains("tap_id")) {
	    					 chargeID = url.split("tap_id=")[1];
	    				 }else {
	    					 chargeID = url.split("checkout/charge/")[1];
	    				 }
	    				 String chargeStatus = getChargeStatus(chargeID);
	    				 System.out.println(((chargeStatus.equalsIgnoreCase("captured")) ? "✅" : "⚠️") + "MADA - SAR "+chargeID+" - "+getChargeStatus(chargeID));
	    				 break;
	    			 }
	    		 }
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}  
	     }
	    
	    driver.close();
	}
	
	
	public static void madaWithBHDFX() {
		ChromeOptions options = new ChromeOptions();
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable( LogType.PERFORMANCE, Level.ALL );
		options.setCapability( "goog:loggingPrefs", logPrefs );
		WebDriver driver = new ChromeDriver(options);
		
		driver.get("https://demo.staging.tap.company/v2/sdk/checkout");
	    driver.manage().window().setSize(new Dimension(1200, 859));
	    // Select the currency
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[2]/div/div[6]/div/div")).click();
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul/li[2]")).click();
	    // Page mode
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[1]/div[5]/button/span[1]/span[1]")).click();
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[2]/div/div[3]/div/div/div")).click();
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul/li[2]")).click();
	    
	    // Start the checkout
	    
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[3]/button")).click();

	    // Wait for the card iFrame to be loaded
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(100000));
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tap-card-iframe")));
	    driver.switchTo().frame("tap-card-iframe");
	    
	    // Enter the card details
	    driver.findElement(By.id("card_input_mini")).sendKeys("4464040000000007");
	    driver.findElement(By.id("date_input")).sendKeys("01/29");
	    driver.findElement(By.id("cvv_input")).sendKeys("100");
	    wait.until(ExpectedConditions.elementToBeClickable(By.id("cardHolderName_input")));
	    driver.findElement(By.id("cardHolderName_input")).sendKeys("OSAMA AHMED HELMY");
	    
	    
	    driver.switchTo().parentFrame();
	    
	    // Click on change to SAR from the Card FX widget            
	    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div[2]/div/div/div[6]/div/div/div/div/div/button")));
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div[2]/div/div/div[6]/div/div/div/div/div/button")).click();
	    // Start the card payment
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div[2]/div/div/div[7]/div/div/div/div/section/button")).click();
	    driver.switchTo().parentFrame();
	    boolean printed = false;
	    wait.until(ExpectedConditions.urlContains("demo.staging"));
	    List<LogEntry> entries = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
	     for (LogEntry entry : entries) {
	    	 try {
	    		 ObjectMapper mapper = new ObjectMapper();
	    		 JsonNode rootNode = mapper.readTree(entry.getMessage());
	    		 if(rootNode.has("message") &&
	    				 rootNode.findValue("message").has("params") &&
	    				 rootNode.findValue("message").findValue("params").has("documentURL")) {
	    			 String url = rootNode.findValue("message").findValue("params").findValue("documentURL").asText();
	    			 String chargeID = "";
	    			 if(url.contains("tap_id") || url.contains("checkout/charge/chg_")) {
	    				 if(url.contains("tap_id")) {
	    					 chargeID = url.split("tap_id=")[1];
	    				 }else {
	    					 chargeID = url.split("checkout/charge/")[1];
	    				 }
	    				 printed = true;
	    				 String chargeStatus = getChargeStatus(chargeID);
	    				 System.out.println(((chargeStatus.equalsIgnoreCase("captured")) ? "✅" : "⚠️") + "MADA - SAR - POPUP - "+chargeID+" - "+getChargeStatus(chargeID));
	    				 break;
	    			 }
	    		 }
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}  
	     }
	    if(!printed) {
	    	System.out.println("⚠️ MADA - SAR - FX");
	    }
	    driver.close();
	}
	
	
	
    public static Map<String, String> getQueryMap(String query)  
	{  
	    String[] params = query.split("&");  
	    Map<String, String> map = new HashMap<String, String>();  
	    for (String param : params)  
	    {  String [] p=param.split("=");
	        String name = p[0];  
	      if(p.length>1)  {String value = p[1];  
	        map.put(name, value);
	      }  
	    }  
	    return map;  
	}
    
    public static String getChargeStatus(String chargeID) {

		OkHttpClient client = new OkHttpClient();
		
		Request request = new Request.Builder()
		    .url("https://api.tap.company/v2/charges/"+chargeID)
		    .header("Authorization", "Bearer sk_test_kovrMB0mupFJXfNZWx6Etg5y")
		    .header("accept", "application/json")
		    .build();
		
		try (Response response = client.newCall(request).execute()) {
		    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
		    ObjectMapper mapper = new ObjectMapper();
   		 	JsonNode rootNode = mapper.readTree(response.body().string());
   		 	return rootNode.findPath("status").asText();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Cannot fetch the charge";
		}
    }
    
}



