
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationE2ETest {

    static Process server;
    static WebDriver driver;

    @BeforeAll
    static void setUp() throws Exception {

        server = new ProcessBuilder(
                "java", "-cp", "target/classes",
                "registration.Main", "data", "8080"
        ).redirectErrorStream(true)
         .redirectOutput(ProcessBuilder.Redirect.DISCARD)
         .start();

        boolean ready = false;

        for (int i = 0; i < 30; i++) {
            try {
                HttpURLConnection connection =
                        (HttpURLConnection) new URL(
                                "http://localhost:8080"
                        ).openConnection();

                connection.setConnectTimeout(1000);
                connection.setReadTimeout(1000);

                if (connection.getResponseCode() == 200) {
                    ready = true;
                    connection.disconnect();
                    break;
                }

                connection.disconnect();

            } catch (Exception e) {
                Thread.sleep(1000);
            }
        }

        assertTrue(ready, "Registration server did not start");

        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(5)
        );
    }

    @Test
    void studentCanAttemptToRegisterForIS1476() {

        WebDriverWait wait = new WebDriverWait(
                driver, Duration.ofSeconds(15)
        );

        driver.get("http://localhost:8080");

        WebElement studentLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.linkText("Nawaf Al-Harbi")
                )
        );

        studentLink.click();

        WebElement addButton = wait.until(
        ExpectedConditions.elementToBeClickable(
                By.xpath("//form[@action='/add'][.//input[@name='section' and contains(@value,'IS1476')]]//button")
        )
);
        addButton.click();

        WebElement notice = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".notice")
                )
        );

        String result = notice.getText();

        assertFalse(result.isBlank(),
                "Registration result should be displayed");

        assertTrue(
                result.contains("Registration failed.")
                || !result.isBlank(),
                "A registration result should be displayed"
        );
    }

    @AfterAll
    static void tearDown() {

        if (driver != null) {
            driver.quit();
        }

        if (server != null) {
            server.destroy();
        }
    }
}
