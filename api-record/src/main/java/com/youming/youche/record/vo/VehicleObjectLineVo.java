package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Date:2022/1/12
 */
@Data
public class VehicleObjectLineVo implements Serializable {

    private Integer sourceProvince;

    private Integer sourceRegion;

    private Integer sourceCounty;

    private Integer desProvince;

    private Integer desRegion;

    private Integer desCounty;

    private Long id;

    private Long carriagePrice;


    private String sourceProvinceName;

    private String sourceRegionName;

    private String sourceCountyName;

    private String desProvinceName;

    private String desRegionName;

    private String desCountyName;


//    private String carriagePriceName;
}
