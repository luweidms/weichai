package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Date:2022/2/19
 */
@Data
public class OrderInfoVo implements Serializable {

     /**
      * 订单号
      */
    private Long orderId;

    /**
     * 挂车车牌
     */
    private String trailerPlate;

    /**
     * 车牌
     */
    private String plateNumber;

    /**
     * 司机手机号
     */
    private String carDriverPhone;

    /**
     * 司机姓名
     */
    private String carDriverMan;


    /**
     * 时间
     */
    private String dependTime;

    /**
     * 超始地
     */
    private Integer sourceRegion;

    /**
     * 超始地
     */
    private String sourceRegionName;

    /**
     * 目标地
     */
    private Integer desRegion;

    /**
     * 目标地
     */
    private String desRegionName;
}
