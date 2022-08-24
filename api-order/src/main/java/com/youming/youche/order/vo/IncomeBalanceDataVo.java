package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class IncomeBalanceDataVo implements Serializable {

    /**
     * 结算方式，1账期，2月结
     */
    private Integer balanceType;

    /**
     * 开票月份
     */
    private Integer invoiceMonth;

    /**
     * 对账期限月
     */
    private Integer reconciliationMonth;

    /**
     * 回单期限月
     */
    private Integer reciveMonth;

    /**
     * 收款期限月
     */
    private Integer collectionMonth;

    /**
     * 回单期限日
     */
    private Integer reciveDay;

    /**
     * 对账期限日
     */
    private Integer reconciliationDay;

    /**
     * 开票期限日
     */
    private Integer invoiceDay;

    /**
     * 收款期限日
     */
    private Integer collectionDay;

    /**
     * 回单期限
     */
    private Integer reciveTime;

    /**
     * 开票期限（天）
     */
    private Integer invoiceTime;


    /**
     * 收款期限
     */
    private Integer collectionTime;

    /**
     * 对账期限
     */
    private Integer reconciliationTime;
}
