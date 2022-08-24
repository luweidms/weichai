package com.youming.youche.record.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询心愿线路
 */
@Data
public class VehicleObjectLineDto implements Serializable {

    private static final long serialVersionUID = -457883545785005055L;

    private Long id;
    private Integer sourceProvince; // 出发省
    private Integer sourceRegion; // 出发市
    private Integer sourceCounty; // 出发区
    private Integer desProvince; // 目的省
    private Integer desRegion; // 目的市
    private Integer desCounty; // 目的区
    private Long carriagePrice; // 承运价

    private String sourceProvinceName;
    private String sourceRegionName;
    private String sourceCountyName;
    private String desProvinceName;
    private String desRegionName;
    private String desCountyName;

}
