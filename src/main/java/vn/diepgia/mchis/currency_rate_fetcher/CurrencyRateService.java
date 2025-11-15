package vn.diepgia.mchis.currency_rate_fetcher;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.time.Duration;
import java.util.List;
import java.util.logging.Logger;

@Service
public class CurrencyRateService {
    @Value("${application.currency.vib-url}")
    private String vibUrl;

    @Value("${application.currency.vcb-url}")
    private String vcbUrl;

    private static final Logger LOGGER = Logger.getLogger(CurrencyRateService.class.getName());

    public CurrencyRates getRates() {
        float vibRate = 0;
        float vcbRate = 0;
        // Java
        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.setBinary("/usr/bin/chromium");
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage",
                "--disable-gpu", "--remote-allow-origins=*", "--single-process");
        options.setImplicitWaitTimeout(Duration.ofSeconds(10));
        WebDriver driver = new ChromeDriver(options);

        // VIB
        try {
            driver.get(vibUrl);
            List<WebElement> elements = driver.findElements(By.className("vib-v2-colum-table-deposit"));
            WebElement element = elements.get(19);
            String value = element.getText();
            value = value.replace(".", "").replace(",", ".");
            vibRate = Float.parseFloat(value);
        } catch (Exception e) {
            LOGGER.severe("Cannot retrieve rate for VIB" + ", exception: " + e.getMessage());
        }
        // VCB
        try {
            driver.get(vcbUrl);
            String xml = driver.getPageSource();
            String currencyCode = "EUR";
            String field = "Transfer";
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            // Harden XML parsing
            f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            f.setFeature("http://xml.org/sax/features/external-general-entities", false);
            f.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            f.setXIncludeAware(false);
            f.setExpandEntityReferences(false);

            DocumentBuilder b = f.newDocumentBuilder();
            Document doc = b.parse(new InputSource(new StringReader(xml)));

            NodeList nodes = doc.getElementsByTagName("Exrate");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element el = (Element) nodes.item(i);
                if (currencyCode.equalsIgnoreCase(el.getAttribute("CurrencyCode"))) {
                    String raw = el.getAttribute(field);
                    if (raw == null || raw.isBlank()) break;
                    // Normalize numbers like "24,560.00" -> "24560.00"
                    String normalized = raw.replace(",", "").trim();
                    vcbRate = Float.parseFloat(normalized);
                    break;
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Cannot retrieve rate for VCB" + ", exception: " + e.getMessage());
        } finally {
            driver.quit();
        }
        return CurrencyRates.builder().vibRate(vibRate).vcbRate(vcbRate).build();
    }
}
