package com.tyrival.selenium.entity.task;

import org.openqa.selenium.WebDriver;
import com.tyrival.selenium.ClassLoader;

import static java.lang.Thread.sleep;

/**
 * @Description: 所有脚本都必须继承的父类
 * @Author: Zhou Chenyu
 * @Date: 2018/3/5
 * @Version: V1.0
 * @Modified By:
 * @Modified Date:
 * @Why:
 */
public class BaseScript {

    public static final boolean SUCCESS = true;
    public static final boolean FAIL = false;
    /**
     * 脚本执行日志
     */
    private Log log;
    /**
     * Selenium驱动
     */
    private WebDriver webDriver;
    /**
     * 测试网站地址
     */
    private String url;
    /**
     * 动态编译器
     */
    private ClassLoader classLoader;

    public BaseScript() {
    }

    public BaseScript(WebDriver webDriver, String url, Log log, ClassLoader classLoader) {
        this.url = url;
        this.webDriver = webDriver;
        this.log = log;
        this.classLoader = classLoader;
    }

    public Boolean executeScript(String className, String method, Object[] params) throws Exception {
        try {
            Class klass = this.classLoader.compiler(className);
            Object instance = klass.newInstance();
            ReflectObject reflectObject = new ReflectObject(instance);
            reflectObject.setMethodValue("webDriver", this.webDriver);
            reflectObject.setMethodValue("url", this.url);
            reflectObject.setMethodValue("log", this.log);
            reflectObject.setMethodValue("classLoader", this.classLoader);
            Boolean result = (Boolean) reflectObject.invokeMethod(method, params);
            return result;
        } catch (Exception e) {
            String log = "脚本内动态调用【" + className + "." + method + "】脚本失败。异常消息：" + e.getMessage();
            this.addErrorLog(log);
            throw new Exception(log);
        }
    }

    /**
     * 等待几秒
     *
     * @param i 秒数
     */
    public void waitSeconds(Integer i) {
        try {
            sleep(i * 1000);
        } catch (Exception e) {

        }
    }

    /**
     * 增加一条日志
     *
     * @param str
     */
    public void addLog(String str) {
        log.addAction(str);
    }

    /**
     * 增加一条错误日志
     *
     * @param str
     */
    public void addErrorLog(String str) {
        log.setError(true);
        log.addAction("错误：" + str);
    }

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public void setWebDriver(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
