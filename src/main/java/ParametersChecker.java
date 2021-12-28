import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.lang.Thread.sleep;

public class ParametersChecker {

    String url;

    WebDriver driver;
    int yaX = -1;
    int yaPages = -1;
    int gPages = -1;
    int bingPages = -1;
    int yaImages = -1;
    int pageInGoogleIndex = -1;
    int pageInYaIndex = -1;
    int incomingLinkpadLinks = -1;
    int outcomingLinkpadLinks = -1;
    int incomingBingLinks = -1;
    int yaMentionings = -1;
    boolean robotsTxt = false;
    boolean sitemapXml = false;
    String siteAddress = "unknown"; //check if "unknown"
    String hosting; //check if empty
    String CMS = "unknown";
    String webArchiveDate = "неизвестно";
    List<String> sitesForIP = new ArrayList<>();


    ParametersChecker() {

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

    ParametersChecker(String url) {

        url = url.replaceAll("http://", "");
        url = url.replaceAll("https://", "");
        url = url.replaceAll("www.", "");
        this.url = url;

        String chromeDriverPath = "chromedriver.exe" ;
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200","--ignore-certificate-errors");
       // options.addArguments("--proxy-server=http://" + "88.218.17.24:3128");
        driver = new ChromeDriver(options);

    }

    public int fetchYaX() {

            try {

                driver.get("https://webmaster.yandex.ru/siteinfo/?host=" + url);
                String urlCheck = driver.getCurrentUrl();

                while (urlCheck.contains("captcha")) {
                    WebElement element = driver.findElement(By.xpath("/html/body/div/form/div[1]/div/div[1]/img"));
                    String imgSrc = element.getAttribute("src");
                    BufferedImage bufferedImage = ImageIO.read(new URL(imgSrc));
                    File imageFile = new File("tempYa.png");
                    ImageIO.write(bufferedImage, "png", imageFile);
                    generateYaCaptchaInterface();
                    urlCheck = driver.getCurrentUrl();

                }

                captureScreenshot();
                WebElement element = driver.findElement(By.xpath("/html/body/div[3]/div/div[1]/div[2]/div/div[3]/div/div[6]/div[1]/div/div"));
                String yaxString = element.getText();
                yaX = Integer.valueOf(yaxString.split(":")[1].replaceAll(" ", "").replaceAll(" ", ""));
            }
            catch (Exception e) {

                e.printStackTrace();
                return yaX;
            }

        return yaX;
    }

    public int fetchYaPages() {

        try {

            driver.get("https://yandex.ru/search/?text=host:" + url + " | host:www." + url);

            if (!driver.findElements(By.xpath("/html/body/div/div/div/div[3]/div/form/div/div[1]/input")).isEmpty()) {

                driver.findElement(By.xpath("/html/body/div/div/div/div[3]/div/form/div/div[1]/input")).click();
                sleep(2000);
                captureScreenshot();
            }

            while (driver.getCurrentUrl().contains("captcha")) {

                System.out.println(driver.getPageSource());
                WebElement element = driver.findElement(By.className("AdvancedCaptcha-Image"));
                String imgSrc = element.getAttribute("src");
                BufferedImage bufferedImage = ImageIO.read(new URL(imgSrc));
                File imageFile = new File("tempYa.png");
                ImageIO.write(bufferedImage, "png", imageFile);
                generateYaCaptchaInterface();

            }

            WebElement element = driver.findElement(By.xpath("/html/body/div[3]/div[1]/div[2]/div[1]/div[2]/div/div[2]"));
            yaPages = convertYaPages(element.getText());

        }
        catch (Exception e) {

            e.printStackTrace();
            return yaPages;
        }

        return yaPages;
    }

    public int fetchGooglePages() {

        String res = "";
        try {

            driver.get("https://www.google.com/search?q=site:" + url);
            WebElement element = driver.findElement(By.xpath("/html/body/div[7]/div/div[7]/div/div/div/div/div"));
            res = element.getText();
        }
        catch (Exception e) {

            e.printStackTrace();
            return gPages;

        }

        res = res.split(" примерно ")[1];
        int i = 0;
        String newRes = "";

        while (res.charAt(i) != '(') {
            newRes = newRes + res.charAt(i);
            i++;
        }
        newRes = newRes.replaceAll(" ", "");

        gPages = Integer.valueOf(newRes);
        return gPages;
    }

    public int fetchBingPages() {

        String res = "";
        try {

            driver.get("https://m.bing.com/search?q=site:" + url);
            sleep(5000);
            WebElement element = driver.findElement(By.className("sb_count"));
            captureScreenshot();

            res = element.getText();
            res = res.split(": ")[1].replaceAll(" ", "");
            bingPages = Integer.valueOf(res);

        }
        catch (Exception e) {

            e.printStackTrace();
            return bingPages;

        }

        return bingPages;
    }

    public int fetchYaImages() {

        String res = "";
        try {

            driver.get("https://yandex.ru/images/search?text=site:" + url);
            res = res = driver.getTitle().split(": ")[1];
            res = res.split(" ")[0] + " " + res.split(" ")[1];
        }
        catch (Exception e) {

            e.printStackTrace();
            return yaImages;
        }

        yaImages = convertYaImages(res);

        return yaImages;
    }

    public int checkPageInGoogleIndex(String addr) {

        try {

            driver.get("https://www.google.com/search?q=site:" + addr);
            WebElement element = driver.findElement(By.xpath("/html/body/div[7]/div/div[7]/div/div/div/div/div"));
            pageInGoogleIndex = 1;
        }
        catch (Exception e) {

            pageInGoogleIndex = 0;
            e.printStackTrace();
        }

        return pageInGoogleIndex;
    }

    public int checkPageInYaIndex(String addr) {

        try {

            driver.get("https://yandex.ru/search/?text=url:" + addr + " | url:www." + addr);
            captureScreenshot();

            if (!driver.findElements(By.xpath("/html/body/div/div/div/div[3]/div/form/div/div[1]/input")).isEmpty()) {

                driver.findElement(By.xpath("/html/body/div/div/div/div[3]/div/form/div/div[1]/input")).click();
                sleep(2000);
                captureScreenshot();
            }

            while (driver.getCurrentUrl().contains("captcha")) {

                System.out.println(driver.getPageSource());
                WebElement element = driver.findElement(By.className("AdvancedCaptcha-Image"));
                String imgSrc = element.getAttribute("src");
                BufferedImage bufferedImage = ImageIO.read(new URL(imgSrc));
                File imageFile = new File("tempYa.png");
                ImageIO.write(bufferedImage, "png", imageFile);
                generateYaCaptchaInterface();

            }

            if (driver.findElements(By.xpath("/html/body/div[3]/div[1]/div[2]/div[1]/div[1]/div[1]/div/div")).isEmpty()) { //по вашему запросу ничего не найдено

                pageInYaIndex = 1;
            }
            else
                pageInYaIndex = 0;

        }
        catch (Exception e) {

            e.printStackTrace();
            return -1;
        }

        return pageInYaIndex;
    }

    public int fetchLinkpadIncomingLinks() {

        try {

            driver.get("https://www.linkpad.ru/?search=" + url + "#/default.aspx?r=3&i=" + url);
            sleep(5000);
            WebElement element = driver.findElement(By.xpath("/html/body/div[5]/div[2]/div/div[3]/div/div[2]/div[1]/div/div[1]/div/table/tbody/tr/td/table[2]/tbody/tr[2]/td[2]/span/a"));
            incomingLinkpadLinks = Integer.valueOf(element.getText().replaceAll(" ", ""));

        }
        catch (Exception e) {


            e.printStackTrace();
            return incomingLinkpadLinks;
        }

        return incomingLinkpadLinks;
    }

    public int fetchLinkpadOutcomingLinks() {

        try {

            driver.get("https://www.linkpad.ru/?search=" + url + "#/default.aspx?r=3&i=" + url);
            sleep(5000);
            WebElement element = driver.findElement(By.xpath("/html/body/div[5]/div[2]/div/div[3]/div/div[2]/div[1]/div/div[1]/div/table/tbody/tr/td/table[3]/tbody/tr[2]/td[2]/span/a"));
            outcomingLinkpadLinks = Integer.valueOf(element.getText().replaceAll(" ", ""));

        }
        catch (Exception e) {

            e.printStackTrace();
            return outcomingLinkpadLinks;
        }

        return outcomingLinkpadLinks;
    }

    public int fetchBingIncomingLinks() {

        try {

            driver.get("https://www.bing.com/search?q=inbody:" + url + "+-site:" + url);
            sleep(5000);
            WebElement element = driver.findElement(By.xpath("/html/body/div[1]/main/div/span[1]"));
            String res = element.getText().split(": ")[1].replaceAll(" ", "");
            incomingBingLinks = Integer.valueOf(res);

        }
        catch (Exception e) {

            e.printStackTrace();
            return incomingBingLinks;
        }

        return incomingBingLinks;
    }

    public int fetchYaMentionings() {

        try {

            driver.get("https://yandex.ru/search/?text=(\"*." + url + "\") ~~ site:" + url);
            String urlCheck = driver.getCurrentUrl();

            while (urlCheck.contains("captcha")) {

                WebElement element = driver.findElement(By.xpath("/html/body/div/form/div[1]/div/div[1]/img"));
                String imgSrc = element.getAttribute("src");
                BufferedImage bufferedImage = ImageIO.read(new URL(imgSrc));
                File imageFile = new File("tempYa.png");
                ImageIO.write(bufferedImage, "png", imageFile);
                generateYaCaptchaInterface();
                urlCheck = driver.getCurrentUrl();

            }

            WebElement element = driver.findElement(By.xpath("/html/body/div[3]/div[1]/div[2]/div[1]/div[2]/div/div[1]"));
            yaMentionings = convertYaPages(element.getText());

        }
        catch (Exception e) {

            e.printStackTrace();
            return yaMentionings;
        }

        return yaMentionings;
    }

    public String fetchSiteAddress() {

        try {

            driver.get("https://2ip.ru/lookup/");
            List<WebElement> pic = driver.findElements(By.xpath("/html/body/div[1]/div[3]/div/div[2]/div[1]/div/div[2]/div/div[1]/form/div[2]/div[2]/img"));
            List<WebElement> result = driver.findElements(By.xpath("/html/body/div[1]/div[3]/div/div[2]/div[1]/div/div[2]/div/p[3]"));
            while ((!pic.isEmpty()) && (result.isEmpty())) {

                captureScreenshot();
                driver.findElement(By.name("ip")).sendKeys("");
                BufferedImage image = ImageIO.read(new File("test.png"));
                BufferedImage out = image.getSubimage(919, 672, 85, 39);
                ImageIO.write(out, "png", new File("2IPtemp.png"));
                generate2IPCaptchaInterface();
                driver.findElement(By.name("ip")).sendKeys(url);
                driver.findElement(By.className("input-button")).click();

                pic = driver.findElements(By.xpath("/html/body/div[1]/div[3]/div/div[2]/div[1]/div/div[2]/div/div[1]/form/div[2]/div[2]/img"));
                result = driver.findElements(By.xpath("/html/body/div[1]/div[3]/div/div[2]/div[1]/div/div[2]/div/p[3]"));

            }

            driver.findElement(By.name("ip")).sendKeys(url);
            driver.findElement(By.className("input-button")).click();
            Thread.sleep(1500);
            WebElement element = driver.findElement(By.xpath("/html/body/div[1]/div[3]/div/div[2]/div[1]/div/div[2]/div/div[2]/div/div/table/tbody/tr[1]/td[2]"));

            siteAddress = element.getText();

        }
        catch (Exception e) {

            e.printStackTrace();
            return "";
        }

        return siteAddress;
    }

    public List<String> fetchSitesByIP() {

        try {

            driver.get("https://www.yougetsignal.com/tools/web-sites-on-web-server/");
            driver.findElement(By.id("remoteAddress")).clear();
            driver.findElement(By.id("remoteAddress")).sendKeys(url);
            driver.findElement(By.className("myButton")).click();

            sleep(5000);
            captureScreenshot();

            if (!driver.findElements(By.xpath("/html/body/div[1]/div/div[2]/div/div/div/div/div/div/div/div/div[1]/div[4]/p[1]")).isEmpty()) {

                WebElement div = driver.findElement(By.id("results"));
                List<WebElement> links = div.findElements(By.tagName("p"));
                links.remove(0);

                for (WebElement link : links) {
                    String addr = link.findElement(By.tagName("a")).getAttribute("href");
                    sitesForIP.add(addr);
                }
            }
            else
                sitesForIP = Collections.emptyList();

        }
        catch (Exception e) {

            e.printStackTrace();
            return Collections.emptyList();
        }

        return sitesForIP;
    }

    public String fetchHosting() {

        try {

            driver.get("https://2ip.ru/guess-hosting/");
            List<WebElement> pic = driver.findElements(By.xpath("/html/body/div[1]/div[3]/div/div[2]/div[1]/div/div[2]/div/div[1]/form/div[2]/div[2]/img"));
            List<WebElement> result = driver.findElements(By.xpath("/html/body/div[1]/div[3]/div/div[2]/div[1]/div/div[2]/div/div[2]/div/p/big"));
            while ((!pic.isEmpty()) && (result.isEmpty())) {

                captureScreenshot();
                driver.findElement(By.className("input-text")).sendKeys("");
                BufferedImage image = ImageIO.read(new File("test.png"));
                BufferedImage out = image.getSubimage(919, 738, 85, 39);
                ImageIO.write(out, "png", new File("2IPtemp.png"));
                generate2IPCaptchaInterface();
                driver.findElement(By.className("input-text")).sendKeys(url);
                driver.findElement(By.className("input-button")).click();

                pic = driver.findElements(By.xpath("/html/body/div[1]/div[3]/div/div[2]/div[1]/div/div[2]/div/div[1]/form/div[2]/div[2]/img"));
                result = driver.findElements(By.xpath("/html/body/div[1]/div[3]/div/div[2]/div[1]/div/div[2]/div/div[2]/div/p/big"));

            }

            driver.findElement(By.className("input-text")).sendKeys(url);
            driver.findElement(By.className("input-button")).click();
            Thread.sleep(1500);
            WebElement element = driver.findElement(By.xpath("/html/body/div[1]/div[3]/div/div[2]/div[1]/div/div[2]/div/div[2]/div/p/big"));
            hosting = element.getText();

        }
        catch (Exception e) {

            e.printStackTrace();
            return "";
        }

        return hosting;
    }

    public String fetchCMS() {

        try {

            driver.get("https://whatcms.org/");
            driver.findElement(By.xpath("/html/body/div/div[2]/div/div[1]/div[1]/div[1]/div[1]/div/form/div[2]/div/div/input")).sendKeys(url);
            driver.findElement(By.xpath("/html/body/div/div[2]/div/div[1]/div[1]/div[1]/div[1]/div/form/div[2]/div/div/div/button[1]/span")).click();
            sleep(3000);
            captureScreenshot();

            String message = driver.findElement(By.xpath("/html/body/div/div[2]/div/div[1]/div[1]/div[1]/div[1]/div/div/div[2]/div[1]")).getText();
            if (message.equals("Success")) {

                WebElement table = driver.findElement(By.xpath("/html/body/div/div[2]/div/div[1]/div[1]/div[1]/div[1]/div/div/div[2]/div[2]/table"));
                List<WebElement> rows = table.findElements(By.tagName("tr"));
                for (WebElement row: rows) {

                    List<WebElement> cols = row.findElements(By.tagName("td"));
                    for (WebElement col : cols) {

                        if (col.getText().contains("CMS")) {

                            CMS = cols.get(cols.indexOf(col) + 1).getText();
                            return CMS;
                        }
                    }
                }
            }
            else {

                CMS = "Неизвестно";
            }

        }

        catch (Exception e) {

            e.printStackTrace();
            return "Неизвестно";
        }

        return CMS;
    }

    public PageInfo fetchPageResponse(String addr) {

        String c, r;

        try {
            driver.get("https://calcus.ru/proverka-otveta-servera");
            captureScreenshot();
            driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[1]/form/div[1]/div[2]/input")).sendKeys(addr);
            driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[1]/form/div[5]/div[1]/input")).click();
            sleep(2000);

            captureScreenshot();
            WebElement panel = driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[1]/form/div[7]/div[2]/div"));
            List<WebElement> responseCodes = panel.findElements(By.className("http-code"));
            WebElement responseCode = responseCodes.get(responseCodes.size() - 1);
            c = responseCode.getText();

            if (!panel.findElements(By.className("params-table")).isEmpty())
                r = panel.findElement(By.className("params-table")).findElements(By.cssSelector("td")).get(1).getText();
            else
                r = "---";

            return new PageInfo(c, r);

        }
        catch (Exception e) {

            e.printStackTrace();
        }

        return new PageInfo("неизвестно", "неизвестно");
    }

    public boolean checkHtmlValidationStatus(String addr) {

        try {
            driver.get("https://validator.w3.org/nu/");
            driver.findElement(By.xpath("/html/body/form/fieldset/div/input")).sendKeys(addr);
            driver.findElement((By.xpath("/html/body/form/fieldset/p[2]/input"))).click();
            sleep(1500);

            WebElement element = driver.findElement(By.xpath("/html/body/div[2]/p[1]"));
            String result = element.getText();
            if (result.equals("Document checking completed."))
                return false;
            else
                return true;
        }
        catch (Exception e) {

            e.printStackTrace();
        }

        return false;

    }

    public boolean checkCssValidationStatus(String addr) {

        try {
            driver.get("https://jigsaw.w3.org/css-validator/");
            driver.findElement(By.xpath("/html/body/div[2]/div/fieldset[1]/form/p[2]/label/input")).sendKeys(addr);
            driver.findElement(By.xpath("/html/body/div[2]/div/fieldset[1]/form/p[3]/label/a/span")).click();
            sleep(1500);

            if (!driver.findElements(By.id("congrats")).isEmpty())
                return true;
            else
                return false;

        }
        catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }

    public String fetchWebArchiveData() {


        try {
            driver.get("https://archive.org/web/");
            captureScreenshot();
            driver.findElement(By.xpath("/html/body/div/main/div/div[1]/form/div[2]/input")).sendKeys(url);
            driver.findElement(By.xpath("/html/body/div/main/div/div[1]/form/div[2]/input")).sendKeys(Keys.ENTER);
            sleep(2000);


            if (!driver.findElements(By.xpath("/html/body/div[4]/div[2]/span/a[1]")).isEmpty()) {

                webArchiveDate = driver.findElement(By.xpath("/html/body/div[4]/div[2]/span/a[1]")).getText();
                webArchiveDate = dateConverter(webArchiveDate);
            }

        }
        catch (Exception e) {

            e.printStackTrace();
        }

        return webArchiveDate;
    }

    public boolean fetchRobotsTxt() {


        PageInfo info = fetchPageResponse(url + "/robots.txt");
        String r = info.getHttpResponse();
        if (r.contains(" "))
            r = r .split(" ")[0];
        int response = Integer.valueOf(r);
        if (response == 200)
            robotsTxt = true;
        else
            robotsTxt = false;

        return robotsTxt;

    }

    public boolean fetchSitemapXml() {


        PageInfo info = fetchPageResponse(url + "/sitemap.xml");
        String r = info.getHttpResponse();
        if (r.contains(" "))
            r = r .split(" ")[0];
        int response = Integer.valueOf(r);
        if (response == 200)
            sitemapXml = true;
        else
            sitemapXml = false;

        return sitemapXml;

    }

    private void generateYaCaptchaInterface() {

        JDialog dialog = new JDialog();
        dialog.setSize(350, 250);
        dialog.setTitle("Введите капчу");
        dialog.setLayout(null);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        JLabel label = new JLabel();
        try {

            BufferedImage myPicture = ImageIO.read(new File("tempYa.png"));
            ImageIcon icon = new ImageIcon(myPicture);
            label.setIcon(icon);
        }
        catch (Exception e) {

            e.printStackTrace();
        }

        label.setBounds(51, 20, 248, 81);

        JTextField field = new JTextField();
        field.setBounds(75, 130, 200, 20);

        JButton button = new JButton("Отправить");
        button.setBounds(110, 175, 130, 20);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    driver.findElement(By.className("Textinput-Control")).sendKeys(field.getText());
                    driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/div[2]/button[3]")).click();
                    dialog.dispose();

                }
                catch (Exception e1) {

                    e1.printStackTrace();
                }
            }
        });

        dialog.add(label);
        dialog.add(field);
        dialog.add(button);
        dialog.setVisible(true);
    }

    private void generate2IPCaptchaInterface() {

        JDialog dialog = new JDialog();
        dialog.setSize(350, 250);
        dialog.setTitle("Введите капчу");
        dialog.setLayout(null);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        JLabel label = new JLabel();
        try {

            BufferedImage myPicture = ImageIO.read(new File("2IPtemp.png"));
            ImageIcon icon = new ImageIcon(myPicture);
            label.setIcon(icon);
        }
        catch (Exception e) {

            e.printStackTrace();
        }

        label.setBounds(120, 20, 248, 81);

        JTextField field = new JTextField();
        field.setBounds(75, 130, 200, 20);

        JButton button = new JButton("Отправить");
        button.setBounds(110, 175, 130, 20);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    driver.findElement(By.name("turingCode")).sendKeys(field.getText());
                    sleep(2000);
                    dialog.dispose();

                }
                catch (Exception e1) {

                    e1.printStackTrace();
                }
            }
        });

        dialog.add(label);
        dialog.add(field);
        dialog.add(button);
        dialog.setVisible(true);
    }

    public WebDriver getDriver() {
        return driver;
    }

    private int convertYaPages(String result) {

        if (result.contains("тыс."))
            yaX = Integer.valueOf(result.split(" ")[1]) * 1000;
        else
            if (result.contains("млн"))
                yaX = Integer.valueOf(result.split(" ")[1]) * 1000000;
            else
                yaX = Integer.valueOf(result.split(" ")[1]);

            return yaX;
    }

    private int convertYaImages(String result) {

        if (result.contains("тыс"))
            yaX = Integer.valueOf(result.split(" ")[0]) * 1000;
        else
        if (result.contains("млн"))
            yaX = Integer.valueOf(result.split(" ")[0]) * 1000000;
        else
            yaX = Integer.valueOf(result.split(" ")[0]);

        return yaX;
    }


    private void captureScreenshot (){

        try {
            TakesScreenshot ts = (TakesScreenshot)driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            String dest = "test.png";
            File destination = new File(dest);
            FileUtils.copyFile(source, destination);

        }

        catch (IOException e) {e.getMessage();}
    }

    private String dateConverter(String date) {

        String newDate = date;
        String year = newDate.split(", ")[1];
        newDate = newDate.split(", ")[0];

        switch (newDate.split(" ")[0]) {

            case "January":
                newDate = newDate.replaceAll("January", "января");
                newDate = newDate.split(" ")[1] + " " + newDate.split(" ")[0];
                break;

            case "February":
                newDate = newDate.replaceAll("February", "февраля");
                newDate = newDate.split(" ")[1] + " " + newDate.split(" ")[0];
                break;

            case "March":
                newDate = newDate.replaceAll("March", "марта");
                newDate = newDate.split(" ")[1] + " " + newDate.split(" ")[0];
                break;

            case "April":
                newDate = newDate.replaceAll("April", "апреля");
                newDate = newDate.split(" ")[1] + " " + newDate.split(" ")[0];
                break;

            case "May":
                newDate = newDate.replaceAll("May", "мая");
                newDate = newDate.split(" ")[1] + " " + newDate.split(" ")[0];
                break;

            case "June":
                newDate = newDate.replaceAll("June", "июня");
                newDate = newDate.split(" ")[1] + " " + newDate.split(" ")[0];
                break;

            case "July":
                newDate = newDate.replaceAll("July", "июля");
                newDate = newDate.split(" ")[1] + " " + newDate.split(" ")[0];
                break;

            case "August":
                newDate = newDate.replaceAll("August", "августа");
                newDate = newDate.split(" ")[1] + " " + newDate.split(" ")[0];
                break;

            case "September":
                newDate = newDate.replaceAll("September", "сентября");
                newDate = newDate.split(" ")[1] + " " + newDate.split(" ")[0];
                break;

            case "October":
                newDate = newDate.replaceAll("October", "октября");
                newDate = newDate.split(" ")[1] + " " + newDate.split(" ")[0];
                break;

            case "November":
                newDate = newDate.replaceAll("November", "ноября");
                newDate = newDate.split(" ")[1] + " " + newDate.split(" ")[0];
                break;

            case "December":
                newDate = newDate.replaceAll("December", "декабря");
                newDate = newDate.split(" ")[1] + " " + newDate.split(" ")[0];
                break;
        }

        return newDate + " " + year;
    }

}
