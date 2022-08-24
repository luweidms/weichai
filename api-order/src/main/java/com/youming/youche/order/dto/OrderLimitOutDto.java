package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
@Data
public class OrderLimitOutDto implements Serializable {
    //订单Id
    private Long orderId;
    //靠台时间
    private String createDate;
    //车牌号
    private String plateNumber;
    //未付欠款
    private long noPayDebt;
    //出发地
    private String sourceRegionName;
    //目的地
    private String desRegionName;




    /**
     * 订单总金额
     */
    private Long totalFee;

    /**
     * 订单现金.
     */
    private Long orderCash;

    /**
     * 订单尾款.
     */
    private Long orderFinal;

    /**
     * 已付尾款(已付).
     */
    private Long paidFinalPay;

    /**
     * 应付尾款(未付).
     */
    private Long noPayFinal;


    /**
     * 运费 = 现金+油+etc
     */
    private long orderPay;

    /**
     * 尾款计划处理时间:签收时间+帐期.
     */
    private Date finalPlanDate;

    /**
     * 处理状态:0初始1完成2失败
     */
    private Integer fianlSts;

    private String logo;

    /**
     * 来源车队名称
     */
    private String sourceName;

    /**
     * 油卡抵押金额
     */
    private Long pledgeOilcardFee;

    private List oilCarList;
}
