package net.mewk.selenium.demo.twitter;

import net.mewk.selenium.demo.launcher.Launcher;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Mewes Kochheim <mewes@kochheim.de>
 */
@RunWith(Parameterized.class)
public class RepositoryTestWin7 {

    private static final Logger LOG = LoggerFactory.getLogger(RepositoryTestWin7.class);

    private WebDriver driver;
    private String baseUrl;
    private String username;
    private String password;

    public RepositoryTestWin7(String baseUrl, Platform platform, String browserName, String version, String username, String password) {
        LOG.info("Starting test:");
        LOG.info("BaseUrl: " + baseUrl);
        LOG.info("Platform: " + platform.toString());
        LOG.info("BrowserName: " + browserName);
        LOG.info("Version: " + version);

        this.baseUrl = baseUrl;
        this.username = username;
        this.password = password;

        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setBrowserName(browserName);
        desiredCapabilities.setPlatform(platform);
        desiredCapabilities.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, true);

        if (version != null) {
            desiredCapabilities.setVersion(version);
        }

        try {
            this.driver = new RemoteWebDriver(new URL(Launcher.getHubUrl()), desiredCapabilities);
            this.driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
            this.driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
            this.driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
            this.driver.manage().window().maximize();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Parameterized.Parameters
    public static Collection environments() {
        return Arrays.asList(new Object[][]{
                {"https://github.com", Platform.VISTA, "chrome", null, "mewes@kochheim.de", "xxx"},
                //{"https://github.com", Platform.VISTA, "firefox", null, "mewes@kochheim.de", "xxx"},
                //{"https://github.com", Platform.WIN8, "chrome", null, "mewes@kochheim.de", "xxx"},
                //{"https://github.com", Platform.WIN8, "firefox", null, "mewes@kochheim.de", "xxx"},
        });
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
    }

    // Tests

    @Test
    public void testFull() throws Exception {
        driver.get(baseUrl + "/");
        assertEquals("Build software better, together.", driver.findElement(By.cssSelector("h1.heading")).getText());
        driver.findElement(By.linkText("Sign in")).click();
        assertEquals("Sign in", driver.findElement(By.cssSelector("h1")).getText());
        driver.findElement(By.id("login_field")).clear();
        driver.findElement(By.id("login_field")).sendKeys(username);
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.name("commit")).click();
        assertEquals("MewesK", driver.findElement(By.cssSelector("span.css-truncate-target")).getText());
        driver.findElement(By.xpath("//div[@id='your_repos']/div/a")).click();
        assertEquals("Repository name", driver.findElement(By.xpath("//form[@id='new_repository']/div[2]/dl[2]/dt/label")).getText());
        driver.findElement(By.id("repository_name")).clear();
        driver.findElement(By.id("repository_name")).sendKeys("test");
        Thread.sleep(1000);
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        Thread.sleep(1000);
        assertEquals("MewesK/test", driver.findElement(By.cssSelector("h1.entry-title.public")).getText());
        driver.findElement(By.xpath("//ul[3]/li/a/span[2]")).click();
        Thread.sleep(1000);
        assertEquals("Settings", driver.findElement(By.cssSelector("h3")).getText());
        driver.findElement(By.linkText("Delete this repository")).click();
        Thread.sleep(1000);
        assertTrue(driver.findElement(By.cssSelector("div.facebox-content.dangerzone > h2.facebox-header")).getText().matches("^Are you ABSOLUTELY sure[\\s\\S]$"));
        driver.findElement(By.cssSelector("div.facebox-content.dangerzone > form.js-normalize-submit > p > input.input-block")).clear();
        driver.findElement(By.cssSelector("div.facebox-content.dangerzone > form.js-normalize-submit > p > input.input-block")).sendKeys("test");
        driver.findElement(By.xpath("(//button[@type='submit'])[5]")).click();
        Thread.sleep(1000);
        assertEquals("Your repository \"MewesK/test\" was successfully deleted.", driver.findElement(By.cssSelector("div.flash.flash-notice")).getText());
        driver.findElement(By.xpath("//ul[@id='user-links']/li[5]/form/button")).click();
        Thread.sleep(1000);
        assertEquals("Build software better, together.", driver.findElement(By.cssSelector("h1.heading")).getText());
    }
}
