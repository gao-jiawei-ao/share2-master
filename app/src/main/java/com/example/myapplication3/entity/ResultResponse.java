package com.example.myapplication3.entity;

public class ResultResponse {
    private int code;
    private String msg;
    private String datas;
//    private List<PictureEntity> datas;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return datas;
    }

    public void setData(String data) {
        this.datas = data;
    }

    @Override
    public String toString() {
        return "ResultResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data='" + datas + '\'' +
                '}';
    }
}
