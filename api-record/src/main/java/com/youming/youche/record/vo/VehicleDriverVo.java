package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Date:2022/2/24
 */
@Data
public class VehicleDriverVo implements Serializable {


    /**
     * 司机
     */
    private String linkman;

    /**
     * 司机手机
     */
    private String mobilePhone;

    /**
     * 车队名称
     */
    private String tenantName;
}
