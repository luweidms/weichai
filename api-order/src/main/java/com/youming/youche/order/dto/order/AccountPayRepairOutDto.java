package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;
/**
 * 司机账户支付维修保养输出
 * @author dacheng
 *
 */
@Data
public class AccountPayRepairOutDto implements Serializable {
    //可提现余额
    private Long balance;
    //即将到期余额
    private Long marginBalance;
    //预支手续费
    private Long advanceFee;
    //维修基金
    private Long repairFund;
    //司机线下支付的现金
    private Long cash;
    //总额
    private Long totalAmount;
}
