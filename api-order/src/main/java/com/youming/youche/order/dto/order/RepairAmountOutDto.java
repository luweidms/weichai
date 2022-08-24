package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;
@Data
public class RepairAmountOutDto  implements Serializable {

    private static final long serialVersionUID = -2599777641572618717L;
    //维修基金
    private Long repairFund;
    //可在本维修站消费的余额
    private Long canUseAmount;
    //不可在本维修站消费的余额
    private Long cannotUseAmount;
    //预支手续费
    private Long advanceFee;
    //消费金额
    private Long consumeAmount;
    //总金额 = 消费金额+预支手续费
    private Long totalConsumeAmount;
}
