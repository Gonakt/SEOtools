import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Thread.sleep;

public class KeywordFinder {

    ChromeDriver driver;
    String word;
    List<String> foundWords = new ArrayList<>();

    KeywordFinder(String word) {

        String chromeDriverPath = "chromedriver.exe" ;
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors");

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
        this.word = word;

    }

    List<String> findKeywords() {

        try {

            driver.get("https://yandex.ru/search/?text=" + word);
            sleep(2000);

            if (!driver.findElements(By.className("related__column")).isEmpty()) {

                WebElement yaRelatedColumn = driver.findElement(By.className("related__column"));
                List<WebElement> yaRelatedWords = yaRelatedColumn.findElements(By.tagName("a"));
                for (WebElement yaRelatedWord : yaRelatedWords) {

                    foundWords.add(yaRelatedWord.getText());
                }
            }

            driver.get("https://www.google.com/search?q=" + word);
            sleep(2000);

            if (!driver.findElements(By.xpath("/html/body/div[7]/div/div[9]/div[1]/div/div[4]/div/div/div/div/div/div/div/div[1]")).isEmpty()) {

                WebElement containter = driver.findElement(By.xpath("/html/body/div[7]/div/div[9]/div[1]/div/div[4]/div/div/div/div/div/div/div"));
                List<WebElement> links = containter.findElements(By.tagName("div"));

                for (WebElement link : links) {

                    if (!link.getText().isEmpty()) {

                        foundWords.add(link.getText());
                    }
                }
            }

            captureScreenshot(driver);
        }
        catch (Exception e) {

            e.printStackTrace();
        }

        return foundWords;
    }


    ChromeDriver getDriver() {

        return driver;
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

    private void captureScreenshotForCaptcha (ChromeDriver driver) {

        try {
            TakesScreenshot ts = (TakesScreenshot)driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            String dest = "recaptcha.png";
            File destination = new File(dest);
            FileUtils.copyFile(source, destination);

        }

        catch (IOException e) {e.getMessage();}
    }

}
