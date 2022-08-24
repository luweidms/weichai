package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class WXShopDto implements Serializable {

    private static final long serialVersionUID = -1412734236880907313L;

    private Long totalBalance;
    private Long totalMarginBalance;
    private Long platformServiceCharge;
    /**
     * 未核销平台金额(分)
     */
    private Long noVerificationAmount;
    private Integer productNum;
    private Integer cooperationWaitAduitNum;
    /**
     * 应收逾期
     */
    private Long receivableOverdueBalance;
    /**
     * 应付逾期金额
     */
    private Long payableOverdueBalance;
    private Long electronicOilCard;
    private Long totalOilBalance;
    private Long lockBalance;
    private Long rechargeOilBalance;
    private Long creditLine;
    private Long usedCreditLine;
    private Boolean isUserBindCard;
}
