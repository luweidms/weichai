package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateOrderPaymentDaysInfoInDto implements Serializable {
    private Integer balanceType;
    /**
     * 账期类型
     */
    private Integer paymentDaysType;
    /**
     * 回单期限（天）
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
    /**
     * 回单期限月
     */
    private Integer reciveMonth;
    /**
     * 开票月份
     */
    private Integer invoiceMonth;
    /**
     * 收款期限月
     */
    private Integer collectionMonth;
    /**
     * 对账期限月
     */
    private Integer reconciliationMonth;
    /**
     * 回单期限日
     */
    private Integer reciveDay;
    /**
     * 开票期限日
     */
    private Integer invoiceDay;
    /**
     * 收款期限日
     */
    private Integer collectionDay;
    /**
     * 对账期限日
     */
    private Integer reconciliationDay;
}
