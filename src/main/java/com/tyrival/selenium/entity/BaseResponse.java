package com.tyrival.selenium.entity;

public class BaseResponse<T> {

    private Boolean success;
	
    private String message;

    private T data;

    public BaseResponse() {
        this.success = true;
        this.message = "成功";
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
