package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CreditRatingRuleDto implements Serializable {

    private static final long serialVersionUID = 414508303738060487L;

    /**
     * 是否可预支 0:不可预支  1：可预支'
     */
    private Integer isAdvance;
    /**
     * 预支手续费比例，如0.0001
     */
    private Float counterFee;
    /**
     * 不可预支提示语
     */
    private String noAdvanceHint;
    /**
     * 预付款比例
     */
    private Float advanceCharge;
    /**
     * 油比例，如0.01
     */
    private Float oilScale;
    /**
     * ETC比例，如0.01 (自由车)
     */
    private Float etcScale;
    /**
     * 智能模式返利比例 (自由车)
     */
    private Float rtnAmtScaleZn;
    /**
     * 报账模式返利比例 (自由车)
     */
    private Float rtnAmtScaleBz;
    /**
     * 承包模式返利比例
     */
    private Float rtnAmtScaleCb;
    /**
     * 结算方式 2-预付+尾款账期，3-预付+尾款月结
     */
    private Integer settleType;
    /**
     * 回单期限
     */
    private Integer receiptPeriod;
    /**
     * 对账期限
     */
    private Integer checkPeriod;
    /**
     * 开票期限
     */
    private Integer billPeriod;
    /**
     * 付款期限
     */
    private Integer payPeriod;

    /**
     *回单月份
     */
    private Integer reciveMonth;
    /**
     *对账月份
     */
    private Integer checkMonth;
    /**
     *开票月份
     */
    private Integer billMonth;
    /**
     *付款月份
     */
    private Integer payMonth;
    /**
     * 回单期限日
     */
    private Integer receiptPeriodDay;
    /**
     * 对账期限日
     */
    private Integer checkPeriodDay;
    /**
     * 开票期限日
     */
    private Integer billPeriodDay;
    /**
     * 付款期限日
     */
    private Integer payPeriodDay;

    /**
     * 会员类型：1社会车、2招商车、3自有车、4、个体户、5、外调合同车
     */
    private Integer carUserType;

    /**
     * 保费1-1500
     */
    private String premium1;
    /**
     * 保费1500-3500
     */
    private String premium2;
    /**
     * 保费3500-8000
     */
    private String premium3;
    /**
     * 保费8000以上
     */
    private String premium4;



}
