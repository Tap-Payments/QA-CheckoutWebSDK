import java.time.Duration;
import java.util.*;
import java.util.logging.Level;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CheckoutStagingConfigsChrome {



	public static void main(String[] args) {
		// TODO Auto-generated method stub
		checkInvalidCustomerID();
		checkLanguage();
		checkCurrencies();
		checkDefaultCurrency();
		checkPaymentType();
	}
	
	public static void checkInvalidCustomerID() {
		ChromeOptions options = new ChromeOptions();
		//SafariOptions options1= new SafariOptions();
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable( LogType.PERFORMANCE, Level.ALL );
		options.setCapability( "goog:loggingPrefs", logPrefs );
		WebDriver driver = new ChromeDriver(options);
		
		driver.get("https://demo.staging.tap.company/v2/sdk/checkout");
	    driver.manage().window().setSize(new Dimension(1200, 859));
	    
	    // Select the currencies
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[1]/div[2]/button")).click();
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[2]/div/div[1]/div/div/input")).sendKeys("sad");
	    
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[1]/div[5]/button")).click();
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[2]/div/div[3]/div/div/div")).click();
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul/li[2]")).click();
	    // Start the checkout
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[3]/button")).click();

	    // Wait for the checkout iFrame to be loaded
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(20000));
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div/h5")));
	    
	    System.out.println("✅ Invalid customer id configs.");
	    driver.close();
	}
	
	public static void checkCurrencies() {
		ChromeOptions options = new ChromeOptions();
		//SafariOptions options1= new SafariOptions();
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable( LogType.PERFORMANCE, Level.ALL );
		options.setCapability( "goog:loggingPrefs", logPrefs );
		WebDriver driver = new ChromeDriver(options);
		
		driver.get("https://demo.staging.tap.company/v2/sdk/checkout");
	    driver.manage().window().setSize(new Dimension(1200, 859));
	    
	    // Select the currencies
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[2]/div/div[3]/div/div/div")).click();
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul/li[2]/label/span[1]/input")).click();
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul/li[3]/label/span[1]/input")).click();
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul/li[4]/label/span[1]/input")).click();
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul/li[5]/label/span[1]/input")).click();
	    driver.findElement(By.xpath("/html/body/div[2]/div[1]")).click();
	    // Start the checkout
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[3]/button")).click();

	    // Wait for the checkout iFrame to be loaded
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(20000));
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div[2]/div/iframe")));
	    driver.switchTo().frame("tap-checkout-sdk-iframe");
	    
	    // Wait for the KNET chip to be rendered
	    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[2]/div/div/div")));
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[2]/div/div/div")).click();
	    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div/ul")));
	    List<WebElement> chipsElements = driver.findElements(By.cssSelector("button[data-testid='CurrencyList-ChipButton']"));
	    System.out.println(((chipsElements.size() == 3) ? "✅" : "⚠️") + " Selected currencies configs.");
	    driver.close();
	}
	
	public static void checkDefaultCurrency() {
		ChromeOptions options = new ChromeOptions();
	//	SafariOptions options1= new SafariOptions();
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable( LogType.PERFORMANCE, Level.ALL );
		options.setCapability( "goog:loggingPrefs", logPrefs );
		WebDriver driver = new ChromeDriver(options);
		
		driver.get("https://demo.staging.tap.company/v2/sdk/checkout");
	    driver.manage().window().setSize(new Dimension(1200, 859));
	    
	    // Select the currencies
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[2]/div/div[6]/div/div/div")).click();
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul/li[1]")).click();
	    
	    // Start the checkout
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[3]/button")).click();

	    // Wait for the checkout iFrame to be loaded
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(20000));
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div[2]/div/iframe")));
	    driver.switchTo().frame("tap-checkout-sdk-iframe");
	    
	    // Wait for the KNET chip to be rendered
	    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[2]/div/div/div")));
	    boolean foundKWD = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[2]/div/div/div/div[1]/h6")).getText().toLowerCase().contains("kd");
	    System.out.println((foundKWD ? "✅" : "⚠️") + " Default currency configs.");
	    driver.close();
	}
	
	
	public static void checkLanguage() {
		ChromeOptions options = new ChromeOptions();
	//	SafariOptions options1= new SafariOptions();
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable( LogType.PERFORMANCE, Level.ALL );
		options.setCapability( "goog:loggingPrefs", logPrefs );
		WebDriver driver = new ChromeDriver(options);
		
		driver.get("https://demo.staging.tap.company/v2/sdk/checkout");
	    driver.manage().window().setSize(new Dimension(1200, 859));
	    
	    // Select the currencies
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[2]/div/div[7]/div/div/div")).click();
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul/li[2]")).click();
	    
	    // Start the checkout
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[3]/button")).click();

	    // Wait for the checkout iFrame to be loaded
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(20000));
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div[2]/div/iframe")));
	    driver.switchTo().frame("tap-checkout-sdk-iframe");
	    	
	    // Wait for the title chip to be rendered
	    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[2]/div/div/div")));
	    boolean foundArabic = driver.findElement(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[4]/div/div/div[2]/div/div/div[1]/div/div/div/div[1]/div/div/div/h6[1]")).getText().toLowerCase().contains("إختر");
	    System.out.println((foundArabic ? "✅" : "⚠️") + " Default language configs.");
	    driver.close();
	}
	
	public static void checkPaymentType() {
		ChromeOptions options = new ChromeOptions();
		//SafariOptions options1= new SafariOptions();
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable( LogType.PERFORMANCE, Level.ALL );
		options.setCapability( "goog:loggingPrefs", logPrefs );
		WebDriver driver = new ChromeDriver(options);
		
		driver.get("https://demo.staging.tap.company/v2/sdk/checkout");
	    driver.manage().window().setSize(new Dimension(1200, 859));
	    
	    // Select the payment type WEB only
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[2]/div/div[4]/div/div/div")).click();
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul/li[1]")).click();
	    
	    // Start the checkout
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[3]/button")).click();

	    // Wait for the checkout iFrame to be loaded
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(20000));
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div[2]/div/iframe")));
	    driver.switchTo().frame("tap-checkout-sdk-iframe");
	    
	    // Wait for the KNET chip to be rendered
	    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[2]/div/div/div")));
	    boolean foundCard = driver.findElements(By.id("tap-card-iframe")).size() == 0;
	    System.out.println((foundCard ? "✅" : "⚠️") + " Selected Payment Types configs.");
	    driver.close();
	}
	
	public static void checkPaymentMethods() {
		ChromeOptions options = new ChromeOptions();
		//SafariOptions options1= new SafariOptions();
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable( LogType.PERFORMANCE, Level.ALL );
		options.setCapability( "goog:loggingPrefs", logPrefs );
		WebDriver driver = new ChromeDriver(options);
		
		driver.get("https://demo.staging.tap.company/v2/sdk/checkout");
	    driver.manage().window().setSize(new Dimension(1200, 859));
	    
	    // Select the payment type KNET & BENEFIT only
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[2]/div/div[5]/div/div/div")).click();
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul/li[9]/label/span[1]/input")).click();
	    driver.findElement(By.xpath("/html/body/div[2]/div[3]/ul/li[4]/label/span[1]/input")).click();
	    
	    // Start the checkout
	    driver.findElement(By.xpath("/html/body/div/div/form/div/div[3]/button")).click();

	    // Wait for the checkout iFrame to be loaded
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(20000));
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div[2]/div/iframe")));
	    driver.switchTo().frame("tap-checkout-sdk-iframe");
	    
	    // Wait for the KNET chip to be rendered
	    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[2]/div/div/div")));
	    boolean foundKNET = driver.findElements(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[3]/div/div/div[2]/div/div/div[1]/div/div/div/div[2]/div[1]/div/div/ul/button[1]")).size() == 0;
	    boolean foundBENEFIT = driver.findElements(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[3]/div/div/div[2]/div/div/div[1]/div/div/div/div[2]/div[1]/div/div/ul/button[2]")).size() == 0;
	    boolean noMoreChips = driver.findElements(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[3]/div[3]/div/div/div[2]/div/div/div[1]/div/div/div/div[2]/div[1]/div/div/ul/button[3]")).size() == 0;
	    System.out.println(((foundBENEFIT && foundKNET && noMoreChips) ? "✅" : "⚠️") + " Selected Payment Methods configs.");
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



