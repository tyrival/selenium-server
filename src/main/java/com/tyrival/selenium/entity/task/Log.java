package com.tyrival.selenium.entity.task;

import com.tyrival.selenium.utils.FileUtil;
import com.tyrival.selenium.utils.PathUtil;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: 测试日志
 * @Author: Zhou Chenyu
 * @Date: 2018/3/5
 * @Version: V1.0
 * @Modified By:
 * @Modified Date:
 * @Why:
 */
public class Log {

    private final String SEPARATOR = "\n";
    private final static String ERROR_EXT = ".error";
    private final static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
    private Date beginTime;
    private List<String> content;
    private Config config;
    private System system;
    private Boolean error;
    private String currrentTaskName;

    public Log(System system, Config config) {
        this.config = config;
        this.system = system;
        this.error = false;
        this.content = new ArrayList<>();
        this.beginTime = new Date();
    }

    public void reset() {
        this.content = new ArrayList<>();
        this.beginTime = new Date();
        this.currrentTaskName = "";
    }

    public void addAction(String str) {
        this.add(this.getNow() + " " + str);
    }

    public void add(String str) {
        content.add(str);
    }

    public String save() throws Exception {
        String docPath = PathUtil.getLogPath();
        String[] datetime = this.formatDate(beginTime).split(" ");
        String[] date = datetime[0].split("-");
        docPath += date[0] + "/" + date[1] + "-" + date[2];
        File root = new File(docPath);
        if (!root.exists()) {
            root.mkdirs();
        }
        String path = docPath + "/";
        String fileName = new StringBuilder()
                .append(this.system.getComment()).append("-")
                .append(this.config.getComment()).append("-")
                .append(this.currrentTaskName).append("-")
                .append(datetime[1].replaceAll(":", "-")
                        .replaceAll("\\.", "-"))
                .append("-").toString();
        String extName = ".txt";
        File logFile = this.createLogFile(path, fileName, extName, 0, this.error);
        BufferedWriter out = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(logFile), "UTF-8"));
        out.write(this.formatContent());
        out.flush();
        out.close();
        this.reset();
        return logFile.getAbsolutePath();
    }

    private File createLogFile(String path, String name, String ext, Integer seq, Boolean error)
            throws Exception {
        String errorExt = error ? ERROR_EXT : "";
        String filePath = new StringBuilder(path)
                .append(name)
                .append(seq)
                .append(errorExt)
                .append(ext).toString();
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
            return file;
        } else {
            seq++;
            return createLogFile(path, name, ext, seq, error);
        }
    }

    public String formatContent() {
        StringBuilder sb = new StringBuilder();
        sb.append("系统名：").append(this.system.getName()).append("-")
                .append(this.system.getComment()).append(SEPARATOR)
                .append("测试场景：").append(this.config.getName()).append("-")
                .append(this.config.getComment()).append(SEPARATOR)
                .append("开始时间：").append(this.formatDate(beginTime)).append(SEPARATOR);
        for (int i = 0; i < this.content.size(); i++) {
            sb.append(this.content.get(i)).append(SEPARATOR);
        }
        return sb.toString();
    }

    public static List<String> listLogPath(String date) {
        String[] array = date.split("-");
        String path = PathUtil.getLogPath() + array[0] + "/" + array[1] + "-" + array[2];
        return FileUtil.listFilePath(path);
    }

    private String getNow() {
        return this.formatDate(new Date());
    }

    private String formatDate(Date date) {
        return SDF.format(date);
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getCurrrentTaskName() {
        return currrentTaskName;
    }

    public void setCurrrentTaskName(String currrentTaskName) {
        this.currrentTaskName = currrentTaskName;
    }
}
