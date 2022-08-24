package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReceivableOverdueBalanceDto implements Serializable {
    /**
     * 应收逾期
     */
    private Long receivableOverdueBalance;
    /**
     * 应付逾期金额
     */
    private Long payableOverdueBalance;
    /**
     * 用户状态
     */
    private String userState;
    /**
     * 用户类型
     */
    private String userType;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 账户状态
     */
    private String accState;
    private Long type;
    private String phone;
    private String name;

    private String marginBalance;
    /**
     * 油账户明细 返回 值
     */
    private String oilBalance;
    /**
     * ETC金额
     */
    private String etcBalance;
    /**
     * 维修基金
     */
    private String repairFund;
    /**
     * 油卡抵押金额
     */
    private String pledgeOilCardFee;
}
