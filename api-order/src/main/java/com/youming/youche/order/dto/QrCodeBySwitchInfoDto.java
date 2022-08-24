package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/5/21
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class QrCodeBySwitchInfoDto implements Serializable {
    private String qrCodeUrl;
    private String qrCodeId;


}
