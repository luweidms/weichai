package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/5/20
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class OrderDriverSwitchInfoDto implements Serializable {
    private String stateName;//状态
    private String formerMileageStr;//原始里程
    private String receiveMileageStr;//接收里程

}
