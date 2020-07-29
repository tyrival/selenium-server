package com.tyrival.selenium.entity.task;

import com.tyrival.selenium.utils.FileUtil;
import com.tyrival.selenium.utils.PathUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: Zhou Chenyu
 * @Date: 2018/3/20
 * @Version: V1.0
 * @Modified By:
 * @Modified Date:
 * @Why:
 */
public class Source {

    public final static String EXT_NAME = ".java";
    private String name;
    private String system;
    private String code;

    public Source(String system, String name) {
        this.system = system;
        this.name = name;
    }

    public Source(String system, String name, String code) {
        this.system = system;
        this.name = name;
        this.code = code;
    }

    public static List<Source> list(String system) {
        String docPath = PathUtil.getSourcePath() + system + "/";
        File doc = new File(docPath);
        File[] files = doc.listFiles();
        if (files == null || files.length <= 0) {
            return null;
        }
        List<Source> list = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            if (!name.endsWith(EXT_NAME)) {
                continue;
            }
            name = name.substring(0, name.lastIndexOf("."));
            Source source = new Source(system, name);
            list.add(source);
        }
        return list;
    }

    public String parseCode() {
        String filePath = PathUtil.getSourcePath() + this.system + "/" + this.name + EXT_NAME;
        File file = new File(filePath);
        String code = "";
        if (file.exists()) {
            code = FileUtil.read(filePath);
        }
        return code;
    }

    public Boolean saveCode() {
        try {
            String filePath = PathUtil.getSourcePath() + this.system + "/" + this.name + EXT_NAME;
            FileUtil.write(filePath, this.code);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
