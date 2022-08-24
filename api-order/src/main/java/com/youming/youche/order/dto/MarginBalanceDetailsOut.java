package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MarginBalanceDetailsOut  implements Serializable {

    private static final long serialVersionUID = 4530021900729482921L;
    //用户id
    private Long userId;
    //可预支金额(分)
    private Long advanceAmount;
    //可预支金额(元)
    private Double advanceAmountDouble;
    //车队名
    private String fleetName;
    //预支手续费比例
    private String proportion;
    //租户id
    private Long tenantId;
}
