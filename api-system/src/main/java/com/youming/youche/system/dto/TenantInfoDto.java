package com.youming.youche.system.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TenantInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tenantName;
    private String tenantLogo;
    private Integer provinceId;
    private Integer cityId;
    private String detailAddr;
    private String shortName;

}
