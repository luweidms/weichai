package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/5/18
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class CmCustomerLineSubwayOutDto implements Serializable {
    /**
     * 历史id
     */
    private Long hisId;
    /**
     * 线路ID
     */
    private Long lineId;
    /**
     * 地铁id
     */
    private Long subwayId;
    /**
     * 客户编号
     */
    private Long customerId;
    /**
     * 目的省份ID
     */
    private Integer desProvince;
    /**
     * 目的城市
     */
    private Integer desCity;
    /**
     * 到达县
     */
    private Integer desCounty;
    /**
     * 目的导航经度
     */
    private String desEand;
    /**
     * 目的地纬度
     */
    private String desNand;
    /**
     * 目的地详细地址
     */
    private String desAddress;
    private Integer seq;
    /**
     * 文字描述目的地址，不填写默认使用百度定位地址
     */
    private String navigatDesLocation;
    /**
     * 到达时限
     */
    private Float arriveTime;
    /**
     * 审核状态 0-待审核，1-审核通过，2-审核拒绝
     */
    private Integer checkState;
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
     * 起始地
     */
    private String source;
    /**
     * 承运方公里数
     */
    private Integer mileageNumber;
}
