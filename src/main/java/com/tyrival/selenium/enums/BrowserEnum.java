package com.tyrival.selenium.enums;

import com.tyrival.selenium.entity.task.Config;
import com.tyrival.selenium.utils.PathUtil;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;

import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: Zhou Chenyu
 * @Date: 2018/3/1
 * @Version: V1.0
 * @Modified By:
 * @Modified Date:
 * @Why:
 */
public enum BrowserEnum {

    CHROME("chrome"),
    IE("ie"),
    FIREFOX("firefox");

    private String name;

    BrowserEnum(String name) {
        this.name = name;
    }

    public static BrowserEnum get(String browser) {
        switch (browser.toLowerCase()) {
            case "chrome":
                return CHROME;
            case "ie":
                return IE;
            case "firefox":
                return FIREFOX;
            default:
                return CHROME;
        }
    }

    public WebDriver initWebDriver(Config config) throws Exception {
        WebDriver driver = this.getWebDriver(config);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        String browserSize = config.getBrowserSize();
        if ("max".equals(browserSize)) {
            driver.manage().window().maximize();
        } else {
            String[] dim = browserSize.split(",");
            try {
                Integer width = Integer.valueOf(dim[0]);
                Integer height = Integer.valueOf(dim[1]);
                driver.manage().window().setSize(new Dimension(width, height));
            } catch (Exception e) {
                throw new Exception("配置参数错误，test.properties中，参数scm.browser.size格式必须为【整数宽度,整数高度】，分隔符为半角逗号。");
            }
        }
        return driver;
    }

    private WebDriver getWebDriver(Config config) throws Exception {
        // 设置浏览器本地路径
        String browserPath = config.getBrowserLocalPath();
        if (!StringUtils.isEmpty(browserPath)) {
            System.setProperty("webdriver." + this.name + ".bin", browserPath);
        }
        // 实例化WebDriver
        String absoluteDriverPath = PathUtil.getDriverPath() + config.getBrowserDriver();
        this.setBrowserDriver(this.name, absoluteDriverPath);
        this.setBrowserLocalPath(this.name, config.getBrowserLocalPath());
        switch (this.name) {
            case "chrome":
                return new ChromeDriver(new ChromeOptions());
            case "firefox":
                return new FirefoxDriver(new FirefoxOptions());
            case "ie":
                return new InternetExplorerDriver(new InternetExplorerOptions());
            default:
                throw new Exception("未选择正确的浏览器");
        }
    }

    private void setBrowserDriver(String browserName, String driverPath) {
        if (!StringUtils.isEmpty(driverPath)) {
            System.setProperty("webdriver." + browserName + ".driver", driverPath);
        }
    }
    private void setBrowserLocalPath(String browserName, String browserLocalPath) {
        if (!StringUtils.isEmpty(browserLocalPath)) {
            System.setProperty("webdriver." + browserName + ".bin", browserLocalPath);
        }
    }
}
