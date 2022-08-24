package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ServiceOrderInfoListDto implements Serializable {

    private static final long serialVersionUID = -1174673145783743251L;

    private Long serviceOrderId;//服务商订单ID
    private String zhaoYouOrderId; // 找油网的订单id
    private LocalDateTime createDate;
    private String productName; // 产品名称
    private Long oilFee; // 消费总金额
    private String plateNumber; // 车牌号
    private Long oilLitre; // 加油升数
    private Integer orderState; // 订单状态:0 支付失败 1 支付成功 2 待支付 3 撤单 4 支付中
    private String orderStateName;
    private Integer orderType; // 订单类型: 1:扫码加油 2:找油网加油
    private Integer isEvaluate; // 0、可评价 1、已评价 2、不可评价

}
