package com.caotu.duanzhi.Http.bean;

public class LoginResponseBean {

    /**
     * code : 1
     * data : 1
     * message : 1
     * sign : 1
     * token : 1
     */

    private String code;
    private String data;
    private String message;
    private String sign;
    private String token;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
