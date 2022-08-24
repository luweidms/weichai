package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LineDataDto implements Serializable {

    private static final long serialVersionUID = 8240236035405610379L;

    private Integer sourceProvince;
    private Integer sourceRegion;
    private Integer sourceCounty;
    private Integer desProvince;
    private Integer desRegion;
    private Integer desCounty;
    private String carriagePrice;

}
