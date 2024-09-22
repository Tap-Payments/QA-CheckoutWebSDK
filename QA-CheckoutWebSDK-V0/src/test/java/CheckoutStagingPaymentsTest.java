
import java.time.Duration;
import java.util.logging.Level;
//import java.util.logging.LoggingPreferences;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import io.qameta.allure.Step;
import io.qameta.allure.Description;

public class CheckoutStagingPaymentsTest {

    WebDriver driver;

    @BeforeMethod
    @Parameters("browser")
    public void setUp(@Optional("chrome") String browser) {
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

    @Test(description = "Test KNET Payment with KWD")
    @Description("This test verifies the KNET payment using KWD currency.")
    public void testKnetWithKWD() {
        driver.get("https://demo.staging.tap.company/v2/sdk/checkout");
        selectCurrency("KWD");
        startCheckout();
        switchToIframe("tap-checkout-sdk-iframe");
        checkKNETChip();
        startKNETPayment();
        System.out.println("✅ KNET payment completed with KWD.");
    }

    @Test(description = "Test KNET Payment with SAR")
    @Description("This test verifies the KNET payment using SAR currency.")
    public void testKnetWithSAR() {
        driver.get("https://demo.staging.tap.company/v2/sdk/checkout");
        selectCurrency("SAR");
        startCheckout();
        switchToIframe("tap-checkout-sdk-iframe");
        checkKNETChip();
        startKNETPayment();
        System.out.println("✅ KNET payment completed with SAR.");
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

    @Step("Starting KNET payment")
    public void startKNETPayment() {
        // Implement the KNET payment steps
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
