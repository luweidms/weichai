package com.youming.youche.record.common;

import org.springframework.stereotype.Component;

/**
 * @Version V1.0
 * @Title BaseController
 * @Package
 * @Description
 * @Data 17:21
 */
@Component
public class BaseController {

//    /**
//     * @Fields ip : 用户登录的IP
//     */
//    public String ip;
//
//
//    @Resource
//    private HttpServletRequest request;
//
//    @DubboReference(version = "1.0.0")
//    IUserService iUserService;
//
//
//    public ResponseResult get() {
//        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
//        return ResponseResult.success(accessToken);
//    }

    /**
     * 操作提示，用于Ajax数据调用
     *
     * @param flag
     * @param message
     * @return Json
     */
    public String operationHintsToJson(Boolean flag, Integer code, String message) {
        String json = "{\"Flag\":" + flag + ",\"Code\":" + code + ",\"Message\":\"" + message + "\"}";
        return json;
    }

    public String operationHintsToJson(Boolean flag, String message) {
        String json = "{\"Flag\":" + flag + ",\"Message\":\"" + message + "\"}";
        return json;
    }

    public String operationHintsToJson(Boolean flag, Integer code, String message, String data) {
        String json = "{\"Flag\":" + flag + ",\"Code\":" + code + ",\"Message\":\"" + message + "\",\"data\":" + data + "}";
        return json;
    }
    public String operationHintsToJson(String data) {
        String json = "{\"data\":" + data + "}";
        return json;
    }
    public String operationHintsToJson(Boolean flag, String message, String data) {
        String json = "{\"Flag\":" + flag + ",\"Message\":\"" + message + "\",\"data\":" + data + "}";
        return json;
    }



}
