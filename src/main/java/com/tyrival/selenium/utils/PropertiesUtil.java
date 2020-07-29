package com.tyrival.selenium.utils;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @Description:
 * @Author: Zhou Chenyu
 * @Date: 2018/3/7
 * @Version: V1.0
 * @Modified By:
 * @Modified Date:
 * @Why:
 */
public class PropertiesUtil {

    public static Properties read(String filePath) throws Exception {
        try {
            Properties properties = new Properties();
            InputStreamReader fis = new InputStreamReader(new FileInputStream(filePath), "UTF-8");
            properties.load(fis);
            return properties;
        } catch (Exception e) {
            throw new Exception("读取配置文件【" + filePath + "】失败，错误信息：" + e.getMessage());
        }
    }
}
