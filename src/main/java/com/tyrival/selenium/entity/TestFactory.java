package com.tyrival.selenium.entity;

import com.tyrival.selenium.entity.task.*;
import com.tyrival.selenium.enums.BrowserEnum;
import com.tyrival.selenium.utils.FileUtil;
import com.tyrival.selenium.utils.PathUtil;
import com.tyrival.selenium.entity.task.System;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 工厂类，功能主入口
 * @Author: Zhou Chenyu
 * @Date: 2018/3/5
 * @Version: V1.0
 * @Modified By:
 * @Modified Date:
 * @Why:
 */
public class TestFactory {

    private final static String CONFIG_BUNDLE_PATH = "config.properties";
    private final static String SYSTEM_BUNDLE_PATH = "system.json";
    private WebDriver webDriver;
    private Log log;
    private ClassLoader classLoader;
    private System system;
    private Config config;

    public TestFactory(String systemName, String config) throws Exception {
        // 解析测试系统配置文件
        this.system = System.parse(PathUtil.getClasspath() + SYSTEM_BUNDLE_PATH, systemName);
        // 解析测试环境配置文件
        String configFilePath = PathUtil.getClasspath() + CONFIG_BUNDLE_PATH;
        this.config = new Config(config, configFilePath);
        BrowserEnum browserEnum = this.config.getBrowser();
        this.webDriver = browserEnum.initWebDriver(this.config);
        this.log = new Log(this.system, this.config);
        this.classLoader = new ClassLoader(this.system, this.config);
    }

    /**
     * 执行多个测试用例
     * @param tasks
     * @param taskParams
     * @return
     * @throws Exception
     */
    public List<String> executeTasks(List<String> tasks, List<List<String[]>> taskParams) throws Exception {
        List<String> logFilePathList = new ArrayList<>();
        if (tasks != null && tasks.size() > 0) {
            for (int i = 0; i < tasks.size(); i++) {
                List<String[]> taskParam = null;
                if (taskParams.size() >= i) {
                    taskParam = taskParams.get(i);
                }
                String logFilePath = executeTask(tasks.get(i), taskParam);
                logFilePathList.add(logFilePath);
            }
        }
        return logFilePathList;
    }


    /**
     * 执行单个测试用例
     * @param taskName
     * @param taskParam
     * @return
     * @throws Exception
     */
    private String executeTask(String taskName, List<String[]> taskParam) throws Exception {
        String logFilePath = null;
        try {
            this.webDriver.get(this.system.getUrl());
            this.log.setCurrrentTaskName(taskName);
            List<Task> taskList = this.system.getTasks();
            if (taskList == null || taskList.size() <= 0) {
                this.log.add("错误：任务列表为空");
                this.log.setError(true);
                throw new Exception("任务列表为空");
            }
            Task task = null;
            for (int i = 0; i < taskList.size(); i++) {
                if (taskName.equals(taskList.get(i).getName())) {
                    task = taskList.get(i);
                    break;
                }
            }
            if (task == null) {
                this.log.add("错误：未找到任务" + taskName);
                this.log.setError(true);
                throw new Exception("未找到任务" + taskName);
            }
            this.log.add("========== 任务开始：" + task.getName() + " : " + task.getComment() + " ==========");
            Boolean flag = true;
            if (task.getSteps() != null && task.getSteps().size() > 0) {
                for (int i = 0; i < task.getSteps().size(); i++) {
                    if (flag != null && !flag) {
                        break;
                    }
                    Step step = task.getSteps().get(i);
                    String[] param = null;
                    if (taskParam.size() >= i) {
                        param = taskParam.get(i);
                    }
                    flag = this.executeStep(step, param);
                }
            }
        } catch (Exception e) {
            String msg = taskName + "任务执行异常，异常信息：" + e.getMessage();
            this.log.add(msg);
            this.log.setError(true);
            throw new Exception(msg);
        } finally {
            // 储存日志
            try {
                logFilePath = this.log.save();
            } catch (Exception e) {
                throw new Exception(taskName + "任务的日志保存失败，错误信息：" + e.getMessage());
            }
        }
        return PathUtil.getRelativePath(logFilePath);
    }

    /**
     * 执行测试用例的步骤
     * @param step
     * @param param
     * @return
     */
    private Boolean executeStep(Step step, String[] param) {
        String module = step.getModule();
        String method = step.getMethod();
        if (StringUtils.isBlank(module)) {
            this.log.add("模块名为空时，脚本名为【" + method + "】");
            return false;
        }
        if (StringUtils.isBlank(method)) {
            this.log.add("模块名为【" + module + "】时，脚本名为空");
            return false;
        }
        try {
            this.log.add("=========> 开始 " + module + "." + method);
            Class klass = this.classLoader.compiler(module);
            Object instance = klass.newInstance();
            ReflectObject reflectObject = new ReflectObject(instance);
            reflectObject.setMethodValue("webDriver", this.webDriver);
            reflectObject.setMethodValue("url", this.system.getUrl());
            reflectObject.setMethodValue("log", this.log);
            reflectObject.setMethodValue("classLoader", this.classLoader);
            Boolean result = (Boolean) reflectObject.invokeMethod(method, param);
            return result;
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            String paramStr = "";
            if (param != null && param.length > 0) {
                sb.append("[");
                for (int i = 0; i < param.length; i++) {
                    sb.append(param[i]).append(",");
                }
                paramStr = sb.substring(0, sb.length() - 1) + "]";
            }
            this.log.addAction("脚本:" + module + "." + method + " 参数:" + paramStr + "执行异常。异常信息：" + e.getMessage());
            this.log.setError(true);
            return false;
        }
    }

    /**
     * 销毁工厂
     */
    public void destroy() {
        // 退出浏览器
        this.webDriver.quit();
    }

    /**
     * 列出所有测试系统信息
     * @return
     */
    public static List<System> listSystem() {
        return System.parse(PathUtil.getClasspath() + SYSTEM_BUNDLE_PATH);
    }

    /**
     * 保存测试系统信息
     * @param content
     * @throws Exception
     */
    public static void saveSystem(String content) throws Exception {
        JSONArray array = JSON.parseArray(content);
        FileUtil.write(PathUtil.getClasspath() + SYSTEM_BUNDLE_PATH, JSON.toJSONString(array, SerializerFeature.PrettyFormat));
    }

    /**
     * 列出所有测试环境
     * @return
     * @throws Exception
     */
    public static List<Config> listConfig() throws Exception {
        try {
            return Config.parse(PathUtil.getClasspath() + CONFIG_BUNDLE_PATH);
        } catch (Exception e) {
            throw new Exception("解析config.properties失败。异常信息：" + e.getMessage());
        }
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public void setWebDriver(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }
}
