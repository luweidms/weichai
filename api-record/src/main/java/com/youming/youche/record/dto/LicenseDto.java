package com.youming.youche.record.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class LicenseDto implements Serializable {
    /**
     * 牌照类型(1:整车，2：拖头)
     */
    private Integer licenceType;
    /**
     * 证照类型（1行驶证年审，2、车辆年审 3 交强险 4商业险 5其他险）
     */
    private Integer annualreviewType;
}
