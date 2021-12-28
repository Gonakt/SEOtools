import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class ValidationChecker {

    String robotsAddress;
    String sitemapAddress;

    ChromeDriver driver;

    String robotsTxtSource = "";
    String sitemapXmlSource = "";
    String htmlSource = "";
    String cssSource = "";
    String normalAddress = "";
    List<RobotsValidationIssue> issues = new ArrayList<>();
    List<HTMLValidationIssue> htmlIssues = new ArrayList<>();
    List<CSSValidationIssue> cssIssues = new ArrayList<>();

    ValidationChecker(String address) {

        normalAddress = address;
        robotsAddress = address + "/robots.txt";
        sitemapAddress = address + "/sitemap.xml";

        String chromeDriverPath = "chromedriver.exe" ;
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200","--ignore-certificate-errors");

        if (new File("settings.txt").exists()) {

            try {

                BufferedReader reader = new BufferedReader(new FileReader(
                        "settings.txt"));

                String line = reader.readLine();

                while (line != null) {

                    line = reader.readLine();
                    if (line.contains("proxy_address=")) {

                        options.addArguments("--proxy-server=http://" + line.split("=")[1]);
                        break;
                    }
                }

                reader.close();
            }
            catch (Exception ex) {

                ex.printStackTrace();
            }
        }

        driver = new ChromeDriver(options);
        driver.manage().deleteAllCookies();
    }

    boolean isRobotsTxtExists() {

        int status = -1;

        try {
            URL url = new URL(robotsAddress);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            status = con.getResponseCode();

        }
        catch (Exception e) {

            e.printStackTrace();
            return false;
        }

        if (status == 200)
            return true;
        else
            return false;

    }

    boolean isSitemapExists() {

        int status = -1;

        try {
            URL url = new URL(robotsAddress);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            status = con.getResponseCode();

        }
        catch (Exception e) {

            e.printStackTrace();
            return false;
        }

        if (status == 200)
            return true;
        else
            return false;
    }

    boolean isPageExists() {

        int status = -1;

        try {
            URL url = new URL(normalAddress);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            status = con.getResponseCode();

        }
        catch (Exception e) {

            e.printStackTrace();
            return false;
        }

        if (status == 200)
            return true;
        else
            return false;
    }

    List<HTMLValidationIssue> validateHTML() {

        try {

            driver.get("https://validator.w3.org/nu/?showsource=yes&doc=" + normalAddress);
            sleep(1000);

            WebElement resultDiv = driver.findElement(By.id("results"));
            WebElement ol = resultDiv.findElements(By.tagName("ol")).get(0);
            List<WebElement> issues = ol.findElements(By.tagName("li"));

            for (WebElement issue : issues) {

                List<WebElement> p = issue.findElements(By.tagName("p"));

                String type = p.get(0).findElement(By.tagName("strong")).getText();
                if (type.equals("Warning"))
                    type = "Предупреждение";
                else
                    type = "Ошибка";

                String description = p.get(0).findElement(By.tagName("span")).getText();

                String row = p.get(1).findElement(By.tagName("a")).findElements(By.tagName("span")).get(0).getText();

                htmlIssues.add(new HTMLValidationIssue(row, type, description));

            }

            WebElement sourceElement = driver.findElement(By.cssSelector("ol[class='source'"));
            List<WebElement> sourceStrings = sourceElement.findElements(By.tagName("li"));

            for (WebElement string : sourceStrings) {

                htmlSource = htmlSource + string.getText().replaceAll("↩", "") + "\n";
            }


        }
        catch (Exception e) {

            e.printStackTrace();
            return null;

        }

        return htmlIssues;
    }

    List<CSSValidationIssue> validateCSS() {

        try {

            driver.get("https://jigsaw.w3.org/css-validator/");
            sleep(1000);
            driver.findElement(By.xpath("/html/body/div[2]/div/fieldset[1]/form/p[2]/label/input")).sendKeys(normalAddress);
            sleep(1000);
            driver.findElement(By.xpath("/html/body/div[2]/div/fieldset[1]/form/p[3]/label/a/span")).click();
            sleep(3000);

            WebElement source = driver.findElement(By.className("vAtRule"));
            List<WebElement> selectors = source.findElements(By.className("selector"));

            for (WebElement selector : selectors) {

                cssSource = cssSource + selector.getText() + "\n\n";
            }

            if (!driver.findElements(By.className("error-section-all")).isEmpty()) {

                List<WebElement> pages = driver.findElement(By.className("error-section-all")).findElements(By.className("error-section"));
                for (WebElement page : pages) {

                    String uri = page.findElement(By.tagName("h4")).findElement(By.tagName("a")).getText();

                    List<WebElement> trs = page.findElements(By.tagName("tr"));

                    for (WebElement tr : trs) {

                        List<WebElement> tds = tr.findElements(By.tagName("td"));

                        String line = tds.get(0).getText();
                        String element = tds.get(1).getText();
                        String description = tds.get(2).getText();
                        String type = "Ошибка";

                        cssIssues.add(new CSSValidationIssue(line, type, description, uri, element));

                    }
                }
            }

            if (!driver.findElements(By.className("warning-section-all")).isEmpty()) {

                List<WebElement> pages = driver.findElement(By.className("warning-section-all")).findElements(By.className("warning-section"));
                for (WebElement page : pages) {

                    String uri = page.findElement(By.tagName("h4")).findElement(By.tagName("a")).getAttribute("href");

                    List<WebElement> trs = page.findElements(By.tagName("tr"));

                    for (WebElement tr : trs) {

                        List<WebElement> tds = tr.findElements(By.tagName("td"));

                        String line = tds.get(0).getText();
                        String element = tds.get(1).getText();
                        String description = tds.get(2).getText();
                        String type = "Предупреждение";

                        cssIssues.add(new CSSValidationIssue(line, type, description, uri, element));

                    }
                }
            }


        }
        catch (Exception e) {

            e.printStackTrace();
            return null;
        }

        return cssIssues;
    }

    List<RobotsValidationIssue> validateRobotsTxt() {

        try {
            driver.get("https://lxrmarketplace.com/robots-txt-validator-tool.html");
            sleep(3000);
            driver.findElement(By.id("domainUrl")).sendKeys(robotsAddress.replaceAll("/robots.txt", ""));
            driver.findElement(By.id("import")).click();
            sleep(5000);

            robotsTxtSource = driver.findElement(By.id("robotsContent")).getAttribute("value");
            WebElement wrapper = driver.findElement(By.id("messageWrapper"));
            List<WebElement> rows = wrapper.findElements(By.tagName("div"));

            for (WebElement row : rows) {

                if (!row.findElements(By.tagName("a")).isEmpty()) {

                    String rowNum = row.getAttribute("id").replaceAll("message", "");
                    String type = row.findElement(By.tagName("i")).getAttribute("class");
                    if (type.contains("triangle"))
                        type = "Предупреждение";
                    else
                        type = "Ошибка";
                    String description = row.findElement(By.tagName("a")).getAttribute("title");

                    issues.add(new RobotsValidationIssue(rowNum, type, description));

                }
            }

        }
        catch (Exception e) {

            e.printStackTrace();
            return null;
        }

        return issues;
    }

    boolean validateSitemapXml() {

        try {

            driver.get(sitemapAddress);
            sitemapXmlSource = driver.findElement(By.tagName("html")).getText();

            driver.get("https://www.xml-sitemaps.com/validate-xml-sitemap.html");
            sleep(2000);
            driver.findElement(By.className("form-control")).sendKeys(sitemapAddress);
            sleep(1000);
            driver.findElement(By.xpath("/html/body/div[1]/div[2]/div/div/div/div[1]/div/div/form/input[3]")).click();
            sleep(3000);

            WebElement table = driver.findElement(By.className("table"));
            List<WebElement> rows = table.findElements(By.tagName("tr"));

            if (rows.get(2).findElement(By.tagName("td")).getText().equals("Yes"))
                return true;
            else
                return false;

        }
        catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }

    int getRobotsWarningsNum() {

        int i = 0;
        for (RobotsValidationIssue issue : issues) {

            if (issue.getType().equals("Предупреждение"))
                i++;
        }

        return i;
    }

    int getRobotsErrorsNum() {

        int i = 0;
        for (RobotsValidationIssue issue : issues) {

            if (issue.getType().equals("Ошибка"))
                i++;
        }

        return i;
    }

    int getHTMLWarningsNum() {

        int i = 0;
        for (HTMLValidationIssue issue : htmlIssues) {

            if (issue.getType().equals("Предупреждение"))
                i++;
        }

        return i;
    }

    int getHTMLErrorsNum() {

        int i = 0;
        for (HTMLValidationIssue issue : htmlIssues) {

            if (issue.getType().equals("Ошибка"))
                i++;
        }

        return i;
    }

    int getCssErrorsNum() {

        int i = 0;
        for (CSSValidationIssue issue : cssIssues) {

            if (issue.getType().equals("Ошибка"))
                i++;
        }

        return i;
    }

    int getCssWarningsNum() {

        int i = 0;
        for (CSSValidationIssue issue : cssIssues) {

            if (issue.getType().equals("Предупреждение"))
                i++;
        }

        return i;
    }

    String getRobotsTxtSource() {

        return robotsTxtSource;
    }

    String getSitemapXmlSource() {

        return sitemapXmlSource;
    }

    String getHtmlSource() {

        return htmlSource;
    }

    String getCssSource() {

        return cssSource;
    }

}

