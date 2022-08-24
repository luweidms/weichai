package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class PayDetailsDto implements Serializable {

    private static final long serialVersionUID = 3271753132280983163L;

    private Long oilFee; // 消费总金额
    private Long serviceOrderId; // 订单id
    private String productName; // 产品名称
    private Long productId; // 产品 id
    private String zhaoYouOrderId; // 找油网的订单id
    private LocalDateTime createDate;
    private Long oilPrice; // 单价
    private Long orgOilPrice; // 油价
    private String plateNumber; // 车牌号
    private Long oilLitre; // 加油升数
    private Long originalFee;
    private Long saveFee;
    private Long balance;

}
