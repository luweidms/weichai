package com.youming.youche.system.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CheckPhoneDto implements Serializable {
    /**
     * 手机号码是否可用
     */
    private Boolean available;

    /**
     * 不可用的原因（只有available为false时，会有数据）
     */
    private String reason;

    /**
     * 手机号码在平台已存在记录时，返回已有的姓名
     */
    private String linkman;

    /**
     * 手机号码在平台已存在记录时，返回已有的身份证号码
     */
    private String identification;

}
