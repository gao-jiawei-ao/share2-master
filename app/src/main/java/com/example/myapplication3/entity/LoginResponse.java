package com.example.myapplication3.entity;

public class LoginResponse {
    /**
     * msg : success
     * code : 0
     * expire : 604800
     * token : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI2IiwiaWF0IjoxNTkyNDg2OTQzLCJleHAiOjE1OTMwOTE3NDN9.f5sxyG60GyDlj0FcZEmPAADiLHX_pATrvicxbADqvRqYurYQC5s0KAjw5XgHS4gpk-qUSwWtcJpY_nJjYf_2Dw
     */

    private String state;
    private int code;   //状态码
    private String token;       //鉴权

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "state='" + state + '\'' +
                ", code=" + code +
                ", token='" + token  + '\'' +
                '}';
    }

    public void setToken(String token) {
        this.token = token;
    }
}
