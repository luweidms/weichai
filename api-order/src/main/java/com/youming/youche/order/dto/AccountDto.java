package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/4/8
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class AccountDto implements Serializable {
    /**
     * 加油使用未到期金额(分)
     */
    private Long marginBalance;
    /**
     * 油账户明细 返回 值
     */
    private Long oilBalance;
    /**
     * ETC金额
     */
    private Long etcBalance;
    /**
     * 维修基金
     */
    private Long repairFund;
    /**
     * 油卡抵押金额
     */
    private Long pledgeOilCardFee;
    /**
     * 应收逾期
     */
    private Long receivableOverdueBalance;
    /**
     * 应付逾期金额
     */
    private Long payableOverdueBalance;
}
