package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class subWayDto implements Serializable {

    /**
     * 目的地详细地址
     */
    private String desAddress;

    /**
     * 到达时限
     */
    private Float arriveTime;

    /**
     * 目的省份ID
     */
    private Integer desProvince;

    /**
     * 目的市编码ID
     */
    private Integer desCity;

    /**
     * 到达县
     */
    private Integer desCounty;

    /**
     * 到达省名称
     */
    private String desProvinceName;

    /**
     * 目的城市
     */
    private String desCityName;

    /**
     * 到达县名称
     */
    private String desCountyName;

    /**
     * 文字描述目的地址，不填写默认使用百度定位地址,做为百度地址的备注
     */
    private String navigatDesLocation;

    /**
     * 目的地经度
     */
    private String desEand;

    /**
     * 目的地纬度
     */
    private String desNand;


    /**
     * 实际靠台时间
     */
    private LocalDateTime carDependDate;



    /**
     * 实际离台时间
     */
    private LocalDateTime carStartDate;


}
