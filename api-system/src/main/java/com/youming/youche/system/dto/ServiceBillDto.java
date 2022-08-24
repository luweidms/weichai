package com.youming.youche.system.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/7/2
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class ServiceBillDto implements Serializable {
    private Long loginAcct;
    private Double billAmount;
    private String serviceProviderName;

}
