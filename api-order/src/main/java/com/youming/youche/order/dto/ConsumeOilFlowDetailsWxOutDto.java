package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: luona
 * @date: 2022/4/24
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class ConsumeOilFlowDetailsWxOutDto implements Serializable {
    /**
     * 地址
     */
    private String address;
    /**
     * 费用
     */
    private Long amount;
    private String belongingFleet;
    /**
     * 客戶名称
     */
    private String consumeName;
    /**
     * 创建时间
     */
    private LocalDateTime createDate;
    private LocalDateTime getDate;
    private Long oilPrice;
    //最多能加油升数(升)
    private Float oilRise;
    /**
     * 订单号
     */
    private String orderId;
    /**
     * 账期
     */
    private int paymentDays;
    /**
     * 平台服务费金额(分)
     */
    private Long platformAmount;
    /**
     * 产品名称
     */
    private String productName;
    /**
     * 对方名称
     */
    private String sourceFleet;
    /**
     * 状态
     */
    private Integer state;
    private String stateName;
    /**
     * 车牌号
     */
    private String plateNumber;
    /**
     * 是否现场价加油 0不是，1是
     */
    private Integer localeBalanceState;
}
