package vn.diepgia.mchis.currency_rate_fetcher;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class WebDriverFactory {
    public static WebDriver create() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--headless=new",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--disable-dev-tools",
                "--no-zygote",
                "--window-size=1920,1080"
        );
        // Uncomment if you see locale / font issues:
        // options.addArguments("--lang=en-US");

        return new ChromeDriver(options);
    }
}
