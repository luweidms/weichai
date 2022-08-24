package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/6/13 13:04
 */
@Data
public class QueryOrderLimitByCndVo implements Serializable {

    private Long userId;
    private String vehicleAffiliation;
    private String hasDebt;
    private String noPayCash;
    private String noPayOil;
    private String noPayFinal;
    private Long orderId;
    private Long tenantId;
    private Integer userType;
}
