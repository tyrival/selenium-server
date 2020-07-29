package com.tyrival.selenium.entity.task;

import com.tyrival.selenium.enums.BrowserEnum;
import com.tyrival.selenium.utils.PropertiesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:
 * @Author: Zhou Chenyu
 * @Date: 2018/3/6
 * @Version: V1.0
 * @Modified By:
 * @Modified Date:
 * @Why:
 */
public class Config {

    private String name;
    private String comment;
    private String browserLocalPath;
    private String browserDriver;
    private String browserSize;
    private BrowserEnum browser;
    /**
     * 匹配xxx.browser参数的正则
     */
    private static final String REG_EX = ".browser$";

    public Config(String configName, String configFilePath) throws Exception {
        this.name = configName;
        Properties properties = PropertiesUtil.read(configFilePath);
        this.comment = properties.getProperty(configName + ".comment");
        this.browserSize = properties.getProperty(configName + ".browser.size").toLowerCase();
        String browserName = properties.getProperty(configName + ".browser").toLowerCase();
        this.browser = BrowserEnum.get(browserName);
        this.browserLocalPath = properties.getProperty("path." + browserName);
        this.browserDriver = properties.getProperty("driver." + browserName);
    }

    public static List<Config> parse(String configFilePath) throws Exception {
        Properties properties = PropertiesUtil.read(configFilePath);
        List<Config> list = new ArrayList<>();
        for (Object key : properties.keySet()) {
            String k = key.toString();
            Pattern p = Pattern.compile(REG_EX);
            Matcher m = p.matcher(k);
            if (m.find()) {
                String configName = k.replace(".browser", "");
                Config config = new Config(configName, configFilePath);
                list.add(config);
            }
        }
        return list;
    }

    public String getBrowserLocalPath() {
        return browserLocalPath;
    }

    public void setBrowserLocalPath(String browserLocalPath) {
        this.browserLocalPath = browserLocalPath;
    }

    public BrowserEnum getBrowser() {
        return browser;
    }

    public void setBrowser(BrowserEnum browserEnum) {
        this.browser = browserEnum;
    }

    public String getBrowserDriver() {
        return browserDriver;
    }

    public void setBrowserDriver(String browserDriver) {
        this.browserDriver = browserDriver;
    }

    public String getBrowserSize() {
        return browserSize;
    }

    public void setBrowserSize(String browserSize) {
        this.browserSize = browserSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
