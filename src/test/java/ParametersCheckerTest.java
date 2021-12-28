import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ParametersCheckerTest extends TestCase {

    ParametersChecker checker = new ParametersChecker("vk.com");

    @Before
    public void initTest() {

        String chromeDriverPath = "chromedriver.exe" ;
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200","--ignore-certificate-errors");
        ChromeDriver driver = new ChromeDriver(options);
    }

    @After
    public void afterTest() {
        checker = null;
    }

    public void testFetchYaPages() {
        assertEquals(1000000, checker.fetchYaPages());
    }

    public void testFetchGooglePages() {
        assertEquals(39000000, checker.fetchGooglePages());
    }

    public void testCheckPageInGoogleIndex() {
        assertEquals(1, checker.checkPageInGoogleIndex("vk.com"));
    }

    public void testCheckPageInYaIndex() {
        assertEquals(1, checker.checkPageInYaIndex("vk.com"));
    }

    public void testFetchLinkpadIncomingLinks() {
        assertEquals(2166530, checker.fetchLinkpadIncomingLinks());
    }

    public void testFetchLinkpadOutcomingLinks() {
        assertEquals(4, checker.fetchLinkpadOutcomingLinks());
    }

    public void testFetchSiteAddress() {
        assertEquals("87.240.190.78", checker.fetchSiteAddress());
    }

    public void testFetchRobotsTxt() {
        assertEquals(true, checker.fetchRobotsTxt());
    }

    public void testFetchSitemapXml() {
        assertEquals(false, checker.fetchSitemapXml());
    }
}