package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderDebtDetail  implements Serializable {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 靠台时间
     */
    private String dependTime;

    /**
     * 欠款金额
     */
    private Long debtMoney;
    /**
     * 已付欠款
     */
    private Long paidDebt;


    /**
     * 未付欠款
     */
    private Long noPayDebt;
}
