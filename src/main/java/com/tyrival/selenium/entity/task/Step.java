package com.tyrival.selenium.entity.task;

import com.alibaba.fastjson.JSONObject;

/**
 * @Description:
 * @Author: Zhou Chenyu
 * @Date: 2018/3/7
 * @Version: V1.0
 * @Modified By:
 * @Modified Date:
 * @Why:
 */
public class Step {
    private String module;
    private String method;

    public Step(String module, String method) {
        this.module = module;
        this.method = method;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
