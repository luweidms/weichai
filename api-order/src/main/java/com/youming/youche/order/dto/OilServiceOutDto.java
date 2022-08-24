package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OilServiceOutDto  implements Serializable {

    private static final long serialVersionUID = -7218595722860101162L;
    //产品id
    private Long productId;
    //服务商用户id
    private Long serviceId;
    //油价(分/升)
    private Long oilPrice;
    //最多能加油升数(升)
    private Float oilRise;
    //可在油站消费总金额(单位分)
    private Long consumeOilBalance;
    //可用油账户在油站消费的金额(单位分)
    private Long oilBalance;
    //平安可提现金额
    private Long pinganBalance;
    //充值油账户在油站消费的金额(单位分)
    private Long rechargeOilBalance;
    private Long tenantId;
    private Long agentServiceId;//代收服务商agentService表主键id

}
