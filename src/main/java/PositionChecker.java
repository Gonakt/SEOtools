import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PositionChecker {

    ChromeDriver driver;
    List<SearchResultPosition> positions = new ArrayList<>();
    List<String> words = new ArrayList<>();
    String address;

    PositionChecker(String address, List<String> words) {

        this.address = address;
        this.words = words;

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

    }

    List<SearchResultPosition> fetchPositions() {

        int i;
        boolean found = false;
        int gPos = -1;

        try {
            for (String word : words) {

                i = 0;
                driver.get("https://www.google.com/search?q=" + word);
                Thread.sleep(2000);

                List<WebElement> divs = driver.findElements(By.cssSelector("div[class='g'"));

                while (i <= 100) {

                    for (WebElement div : divs) {

                        WebElement link = div.findElements(By.tagName("a")).get(0);
                        if (link.getAttribute("href").contains(address)) {

                            i++;
                            gPos = i;
                            found = true;
                            break;

                        } else {

                            i++;
                        }

                    }

                    if (found) {

                        found = false;
                        i = 0;

                        boolean alreadyAdded = false;

                        for (SearchResultPosition pos : positions) {

                            if (pos.getSite().equals(address) && pos.getQuery().equals(word)) {

                                alreadyAdded = true;
                                pos.setgPosition(gPos);
                            }

                        }

                        if (!alreadyAdded)
                            positions.add(new SearchResultPosition(address, word, -1, gPos));

                        break;
                    }
                    else {

                        driver.findElement(By.cssSelector("span[style='display:block;margin-left:53px']")).click();
                        Thread.sleep(2000);
                        divs = driver.findElements(By.cssSelector("div[class='g'"));
                    }
                }

            }
        }
        catch (Exception e) {

            e.printStackTrace();
            return null;
        }

        i = 0;
        found = false;
        int yaPos = -1;

        try {
            for (String word : words) {

                i = 0;
                driver.get("https://yandex.ru/search/?text=" + word);
                Thread.sleep(2000);

                List<WebElement> lis = driver.findElements(By.cssSelector("li[class='serp-item'"));

                while (i <= 100) {

                    for (WebElement li : lis) {

                        String description = li.getText();
                        if (description.contains("реклама")) {

                            continue;
                        } else {

                            WebElement link = li.findElements(By.tagName("a")).get(1);
                            if (link.getText().contains(address)) {

                                i++;
                                yaPos = i;
                                found = true;
                                break;

                            } else {

                                i++;
                            }
                        }
                    }

                    if (found) {

                        found = false;
                        i = 0;

                        boolean alreadyAdded = false;

                        for (SearchResultPosition pos : positions) {

                            if (pos.getSite().equals(address) && pos.getQuery().equals(word)) {

                                alreadyAdded = true;
                                pos.setYaPosition(yaPos);
                            }

                        }

                        if (!alreadyAdded)
                            positions.add(new SearchResultPosition(address, word, yaPos, -1));

                        break;
                    }
                    else {

                        driver.findElement(By.cssSelector("a[aria-label='Следующая страница'")).click();
                        Thread.sleep(2000);
                        lis = driver.findElements(By.cssSelector("li[class='serp-item'"));
                    }
                }

            }
        }
        catch (Exception e) {

            e.printStackTrace();
            return null;
        }

        return positions;

    }

}
