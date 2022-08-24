package com.youming.youche.record.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BackUserDto implements Serializable {

    /**
     * 名称
     */
    private String linkman;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 手机号码
     */
    private String mobiPhone;
    /**
     * 车辆类型
     */
    private Integer carUserType;

    /**
     *车辆类型名称
     */
    private String carUserTypeName;
}
