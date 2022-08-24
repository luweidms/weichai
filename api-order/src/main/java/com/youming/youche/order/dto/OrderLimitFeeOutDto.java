package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderLimitFeeOutDto implements Serializable {


    private static final long serialVersionUID = 5682785103037393579L;


    private Long orderId;//订单号
    private Long userId;//订单用户Id
    private Long useCash;//已用现金
    private Long useOil;//已用油
    private Long useEtc;//已用etc
    private Long noPayOil;//未付油
    private Long noPayEtc;//未付etc
    private Long noPayCash;//未付现金
    private Long useFinal;//已用尾款
    private Long noPayFinal;//未付尾款

}
