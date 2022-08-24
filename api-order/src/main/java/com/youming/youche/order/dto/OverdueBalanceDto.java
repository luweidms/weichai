package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OverdueBalanceDto implements Serializable {
    /**
     * 应收逾期
     */
    private Long receivableOverdueBalance;
    /**
     * 应付逾期金额
     */
    private Long payableOverdueBalance;
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
    private String name;
}
