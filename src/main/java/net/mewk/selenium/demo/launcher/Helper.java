package net.mewk.selenium.demo.launcher;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 * @author Mewes Kochheim <mewes@kochheim.de>
 */
public class Helper {

    public static void injectScript(WebDriver driver, String code) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("javascript:" + code);
    }

    public static void scroll(WebDriver driver, int x, int y) {
        Helper.injectScript(driver, String.format("window.scrollBy(%d, %d)", x, y));
    }
}
