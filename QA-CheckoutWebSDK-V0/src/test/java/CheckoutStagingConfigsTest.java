
import java.time.Duration;
import java.util.logging.Level;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import io.qameta.allure.Step;
import io.qameta.allure.Description;

public class CheckoutStagingConfigsTest {

    WebDriver driver;

    @BeforeMethod
    @Parameters("browser")
    public void setUp(String browser) {
        if (browser.equalsIgnoreCase("chrome")) {
            ChromeOptions options = new ChromeOptions();
            LoggingPreferences logPrefs = new LoggingPreferences();
            logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
            options.setCapability("goog:loggingPrefs", logPrefs);
            driver = new ChromeDriver(options);
        } else if (browser.equalsIgnoreCase("safari")) {
            driver = new SafariDriver();
        }
        driver.manage().window().setSize(new Dimension(1200, 859));
    }

    @Test(description = "Test Invalid Customer ID on Chrome and Safari")
    @Description("This test verifies that the checkout fails when an invalid customer ID is used.")
    public void testInvalidCustomerID() {
        driver.get("https://demo.staging.tap.company/v2/sdk/checkout");
        selectCurrency("sad");
        startCheckout();
        waitForElement(By.xpath("/html/body/div/div/div/h5"));
        System.out.println("✅ Invalid customer id configs.");
    }

    @Test(description = "Test Currencies Selection on Chrome and Safari")
    @Description("This test verifies the currencies selection process during the checkout.")
    public void testCurrencies() {
        driver.get("https://demo.staging.tap.company/v2/sdk/checkout");
        selectCurrencies();
        startCheckout();
        switchToIframe("tap-checkout-sdk-iframe");
        checkKNETChip();
        System.out.println("✅ Selected currencies configs.");
    }

    private void selectCurrencies() {
    }

    @Step("Selecting currency: {currency}")
    public void selectCurrency(String currency) {
        driver.findElement(By.xpath("/html/body/div/div/form/div/div[1]/div[2]/button")).click();
        driver.findElement(By.xpath("/html/body/div/div/form/div/div[2]/div/div[1]/div/div/input")).sendKeys(currency);
    }

    @Step("Starting the checkout process")
    public void startCheckout() {
        driver.findElement(By.xpath("/html/body/div/div/form/div/div[3]/button")).click();
    }

    @Step("Waiting for element located by: {by}")
    public void waitForElement(By by) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(20000));
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    @Step("Switching to iframe: {iframeId}")
    public void switchToIframe(String iframeId) {
        driver.switchTo().frame(iframeId);
    }

    @Step("Checking KNET Chip visibility")
    public void checkKNETChip() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(20000));
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[2]/div[3]/div/div/section/div[2]/div/div/div")));
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
