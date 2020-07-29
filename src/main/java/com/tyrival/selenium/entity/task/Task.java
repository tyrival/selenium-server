package com.tyrival.selenium.entity.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: Zhou Chenyu
 * @Date: 2018/3/7
 * @Version: V1.0
 * @Modified By:
 * @Modified Date:
 * @Why:
 */
public class Task {

    private String name;
    private String comment;
    private List<Step> steps;
    private List<String[]> params;

    public Task(JSONObject object) {
        this.name = object.getString("name");
        this.comment = object.getString("comment");
        this.steps = new ArrayList<>();
        this.params = new ArrayList<>();
        JSONArray stepArray = (JSONArray) object.get("steps");
        JSONArray paramArray = (JSONArray) object.get("params");
        if (stepArray != null && stepArray.size() > 0) {
            for (int i = 0; i < stepArray.size(); i++) {
                JSONObject obj = stepArray.getJSONObject(i);
                String module = obj.getString("module");
                String method = obj.getString("method");
                this.steps.add(new Step(module, method));

                if (paramArray != null && paramArray.size() > i) {
                    JSONArray arr = paramArray.getJSONArray(i);
                    if (arr != null && arr.size() > 0) {
                        String[] param = new String[arr.size()];
                        for (int j = 0; j < arr.size(); j++) {
                            param[j] = arr.getString(j);
                        }
                        this.params.add(param);
                    } else {
                        this.params.add(null);
                    }
                } else {
                    this.params.add(null);
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String[]> getParams() {
        return params;
    }

    public void setParams(List<String[]> params) {
        this.params = params;
    }
}
