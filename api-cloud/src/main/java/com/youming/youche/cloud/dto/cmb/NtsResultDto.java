package com.youming.youche.cloud.dto.cmb;

import java.io.Serializable;

/**
 * @ClassName NtsResultVo
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/12 17:05
 */
public class NtsResultDto<T> implements Serializable {

    /**
     * 存放数据对象
     */
    private T data;

    /**
     * 存放返回码
     */
    private String code;

    /**
     * 存放返回信息
     */
    private String msg;

    /**
     * 子错误码
     */
    private String subCode;

    /**
     * 子错误信息
     */
    private String subMsg;

    /**
     * 是否执行成功
     */
    private boolean success;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSubCode() {
        return subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }

    public String getSubMsg() {
        return subMsg;
    }

    public void setSubMsg(String subMsg) {
        this.subMsg = subMsg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NtsResult{");
        sb.append("data=").append(data);
        sb.append(", code='").append(code).append('\'');
        sb.append(", msg='").append(msg).append('\'');
        sb.append(", subCode='").append(subCode).append('\'');
        sb.append(", subMsg='").append(subMsg).append('\'');
        sb.append(", success=").append(success);
        sb.append('}');
        return sb.toString();
    }
}