import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static java.lang.Thread.sleep;

public class TextQualityChecker {

    String text;
    ChromeDriver etxtDriver;
    ChromeDriver textRuDriver;
    boolean etxtInProgress = false;

    TextQualityChecker(String text) {

        this.text = text;

    }

    boolean checkUniqueEtxt() {

        String chromeDriverPath = "chromedriver.exe" ;
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200","--ignore-certificate-errors");
      //  options.addArguments("--proxy-server=https://131.153.150.226:8110");
        etxtDriver = new ChromeDriver(options);
        try {

            etxtDriver.get("https://www.etxt.ru/antiplagiat/");
            sleep(5000);
            etxtDriver.findElement(By.xpath("/html/body/div[3]/section[2]/div/div/div[3]/form/textarea")).sendKeys(text);
            etxtDriver.findElement(By.xpath("/html/body/div[3]/section[2]/div/div/div[3]/form/div/div[2]/input")).click();
            sleep(5000);

            captureScreenshot(etxtDriver);

        }
        catch (Exception e) {

            e.printStackTrace();
            return false;
        }

        return true;
    }

    boolean checkUniqueTextRu() {

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

        textRuDriver = new ChromeDriver(options);

        try {

            textRuDriver.get("https://text.ru");

            if (textRuDriver.findElements(By.id("xrtg-step-iframe")).size() > 0) {

                textRuDriver.switchTo().frame(textRuDriver.findElement(By.id("xrtg-step-iframe")));
                textRuDriver.findElement(By.xpath("/html/body/div[2]/div/div/a[1]")).click();
                sleep(2000);
                textRuDriver.switchTo().defaultContent();
                captureScreenshot(textRuDriver);

            }

            textRuDriver.findElement(By.name("user-text")).sendKeys(text);
            textRuDriver.findElement(By.xpath("/html/body/div[3]/div[4]/form/div/div[2]/button")).click();
            sleep(2000);
            captureScreenshot(textRuDriver);

        }
        catch (Exception e) {

            e.printStackTrace();
            textRuDriver.quit();
            return false;

        }

        return true;
    }

    TextRuUnique getTextRuStatus() {

        TextRuUnique unique = new TextRuUnique();
        try {

            if (textRuDriver.findElements(By.xpath("/html/xrtg-app/div[2]/div/iframe")).size() > 0) {

                textRuDriver.switchTo().frame(textRuDriver.findElement(By.xpath("/html/xrtg-app/div[2]/div/iframe")));
                textRuDriver.findElement(By.xpath("/html/body/div/div/div/div[1]/a[1]")).click();
                sleep(1500);
                textRuDriver.switchTo().defaultContent();
                sleep(1500);
                System.out.println("found");
                captureScreenshot(textRuDriver);

            }

            if (textRuDriver.findElements(By.xpath("/html/body/div[3]/div[5]/div[1]/div/div[2]/div/div[1]/strong/span")).size() == 0) {

                unique.setQueue("Текст находится в очереди на проверку...");
                unique.setStatus("in progress");
                captureScreenshot(textRuDriver);

            }
            else {

                captureScreenshot(textRuDriver);
                unique.setStatus("success");
                unique.setSpam(textRuDriver.findElement(By.id("tab-seo__act-show-spam")).getText());
                unique.setWater(textRuDriver.findElement(By.id("tab-seo__act-show-water")).getText());
                unique.setUnique(textRuDriver.findElement(By.xpath("/html/body/div[3]/div[5]/div[1]/div/div[2]/div/div[1]/strong/span")).getText());

                WebElement divElement = textRuDriver.findElement(By.xpath("/html/body/div[3]/div[5]/div[1]/div/div[2]/div/div[2]"));
                List<WebElement> links = divElement.findElements(By.className("check-menu__domain"));
                for (WebElement link : links) {

                    unique.getLinks().add(new PlagiatLink(link.findElement(By.tagName("a")).getText(), link.findElement(By.tagName("span")).getText()));
                }


            }
        }
        catch (Exception e) {

            e.printStackTrace();
            textRuDriver.quit();
            unique.setStatus("error");
            return unique;
        }

        return unique;
    }

    EtxtTextUnique getEtxtStatus() {

        try {
            if (etxtDriver.findElements(By.xpath("/html/body/div[4]/div/div[2]/b")).isEmpty()) {

                captureScreenshot(etxtDriver);
                String queue;
                if (etxtDriver.findElement(By.xpath("/html/body/div[3]/div[6]")).isDisplayed())
                    queue = etxtDriver.findElement(By.xpath("/html/body/div[3]/div[6]")).getText();
                else
                    queue = etxtDriver.findElement(By.xpath("/html/body/div[3]/div[5]")).getText();

                String checkPercent = etxtDriver.findElement(By.xpath("/html/body/div[3]/div[4]/span[2]")).getText();

                EtxtTextUnique unique = new EtxtTextUnique();
                unique.setMsg("in progress");
                unique.setQueue(queue);
                unique.setCheckPercent(checkPercent);

                return unique;
            }
            else {

                EtxtTextUnique unique = new EtxtTextUnique();
                unique.setMsg("success");

                captureScreenshot(etxtDriver);
                String uniquePercent = etxtDriver.findElement(By.xpath("/html/body/div[4]/div/div[2]/b")).getText();
                unique.setUnique(uniquePercent);

                WebElement divResult = etxtDriver.findElement(By.xpath("/html/body/div[4]/div/div[8]"));
                List<WebElement> detailedResults = divResult.findElements(By.className("mod-check__page-result-block"));
                for (WebElement element : detailedResults) {
                    String link = "";
                    if (!element.findElements(By.cssSelector("a")).isEmpty()) {

                        link = element.findElement(By.cssSelector("a")).getText();
                    }
                    else continue;

                    String samePercent = element.findElement(By.className("mod-highlight")).getText();

                    unique.getLink().add(new PlagiatLink(link, samePercent));
                }

                return unique;
            }

        }
        catch (Exception e) {

            EtxtTextUnique unique = new EtxtTextUnique();
            unique.setMsg("error");
            e.printStackTrace();

            return unique;
        }
    }

    private void captureScreenshot (ChromeDriver driver){

        try {
            TakesScreenshot ts = (TakesScreenshot)driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            String dest = "test.png";
            File destination = new File(dest);
            FileUtils.copyFile(source, destination);

        }

        catch (IOException e) {e.getMessage();}
    }

    public ChromeDriver getDriver() {

        return etxtDriver;
    }

}
