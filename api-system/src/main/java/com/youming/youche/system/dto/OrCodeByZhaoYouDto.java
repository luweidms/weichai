package com.youming.youche.system.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrCodeByZhaoYouDto implements Serializable {

    private static final long serialVersionUID = -6022138365284697344L;

    private String qrCodeUrl;
    private String qrCodeId;
    private String tenantName;

}
