package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class OilServiceInDto implements Serializable {

    private static final long serialVersionUID = -3873705108584658923L;
    //产品id
    private Long productId;
    //服务商用户id
    private Long serviceId;
    //油价(分/升)
    private Long oilPrice;
    //最多能加油升数(升)
    private Float oilRise;
    //可在油站消费金额(单位分)
    private Long consumeOilBalance;
}

