package com.youming.youche.cloud.cmb.midsrv.jyt.comment.enums;

/**
 * 四种访问方式枚举
 * */
public enum HttpMethod {

    POST("post"),
    GET("get"),
    PUT("put"),
    DELETE("delete");

    private String method;

    public String getMethod() {
        return method;
    }

    HttpMethod(String method) {
        this.method = method;
    }


}
