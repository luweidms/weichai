package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class DispatchBalanceDataVo implements Serializable {
    /**
     * '结算方式，1账期，2月结',
     */
    private Integer balanceType;
    /**
     * 开票期限月
     */
    private Integer invoiceMonth;

    /**
     * 对账月份
     */
    private Integer reconciliationMonth;
    /**
     *回单月份
     */
    private Integer reciveMonth;

    private Boolean isCost;
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
     * 回单期限（天）
     */
    private Integer reciveTime;


    /**
     * 对账期限
     */
    private Integer reconciliationTime;


    /**
     * 开票期限（天）
     */
    private Integer invoiceTime;


    /**
     * 收款期限（天）
     */
    private Integer collectionTime;

    /**
     * 客户id
     */
    private Long customUserId;

    /**
     * 公司名称
     */
    private String companyName;


}
