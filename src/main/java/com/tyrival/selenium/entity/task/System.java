package com.tyrival.selenium.entity.task;

import com.tyrival.selenium.utils.FileUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: Zhou Chenyu
 * @Date: 2018/3/9
 * @Version: V1.0
 * @Modified By:
 * @Modified Date:
 * @Why:
 */
public class System {
    private String name;
    private String url;
    private String comment;
    private String[] jars;
    private List<Task> tasks;

    public System() {
        this.tasks = new ArrayList<>();
    }

    public System(JSONObject object) {
        this.tasks = new ArrayList<>();
        this.name = object.getString("name");
        this.url = object.getString("url");
        this.comment = object.getString("comment");
        JSONArray jarArray = object.getJSONArray("jars");
        if (jarArray != null && jarArray.size() > 0) {
            this.jars = new String[jarArray.size()];
            for (int i = 0; i < jarArray.size(); i++) {
                this.jars[i] = jarArray.getString(i);
            }
        }
        JSONArray taskArray = object.getJSONArray("tasks");
        if (taskArray == null || taskArray.size() <= 0) {
            return;
        }
        for (int i = 0; i < taskArray.size(); i++) {
            JSONObject taskObj = taskArray.getJSONObject(i);
            Task task = new Task(taskObj);
            this.tasks.add(task);
        }
    }

    public static System parse(String systemFilePath, String name) {
        String content = FileUtil.read(systemFilePath);
        JSONArray array = JSONObject.parseArray(content);
        if (array == null || array.size() <= 0) {
            return null;
        }
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i);
            if (name.equals(object.get("name"))) {
                System system = new System(object);
                return system;
            }
        }
        return null;
    }

    public static List<System> parse(String systemFilePath) {
        String content = FileUtil.read(systemFilePath);
        if (StringUtils.isBlank(content)) {
            return new ArrayList<>();
        }
        JSONArray array = JSONObject.parseArray(content);
        if (array == null || array.size() <= 0) {
            return new ArrayList<>();
        }
        List<System> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i);
            System system = new System(object);
            list.add(system);
        }
        return list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public String[] getJars() {
        return jars;
    }

    public void setJars(String[] jars) {
        this.jars = jars;
    }
}
