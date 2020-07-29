package com.tyrival.selenium.utils;

/**
 * @Description:
 * @Author: Zhou Chenyu
 * @Date: 2018/3/7
 * @Version: V1.0
 * @Modified By:
 * @Modified Date:
 * @Why:
 */
public class PathUtil {

    public static String getClasspath() {
        return Thread.currentThread().getContextClassLoader().getResource("").getPath();
    }

    public static String getRelativePath(String absolutePath) {
        return absolutePath.replace(System.getProperty("selenium.webapp"), "");
    }

    public static String getRootPath() {
        return System.getProperty("selenium.webapp");
    }
    public static String getLibPath() {
        return System.getProperty("selenium.webapp") + "WEB-INF/lib/";
    }
    public static String getLogPath() {
        return System.getProperty("selenium.webapp") + "logs/";
    }
    public static String getSourcePath() {
        return getClasspath() + "source/";
    }
    public static String getDriverPath() {
        return getClasspath() + "driver/";
    }
    public static String getTemplatePath() {
        return getClasspath() + "template/";
    }
}
