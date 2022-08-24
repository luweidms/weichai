package com.youming.youche.order.domain;


import com.baomidou.mybatisplus.annotation.TableField;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
* <p>
    *
    * </p>
* @author liangyan
* @since 2022-03-04
*/
    @Data
    @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class CreditRatingRule extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;


            /**
            * 预付款比例
            */
    private Float advanceCharge;

            /**
            * 开票期限
            */
    private Integer billPeriod;

            /**
            * 开票期限日
            */
    private Integer billPeriodDay;

            /**
            * 会员类型：1社会车、2招商车、3自有车、4、个体户、5、外调合同车
            */
    private Integer carUserType;

            /**
            * 对账期限
            */
    private Integer checkPeriod;

            /**
            * 对账期限日
            */
    private Integer checkPeriodDay;

            /**
            * 预支手续费比例，如0.0001
            */
    private Float counterFee;

            /**
            * ETC比例，如0.01
            */
    private Float etcScale;

            /**
            * 是否可预支 0:不可预支  1：可预支
            */
    private Integer isAdvance;

            /**
            * 是否可提现  0:不可提现  1：可提现
            */
    private Integer isWithdrawal;

            /**
            * 等级值，1铜、2银、3金、4钻,-1表示忽略该值所有等级一致
            */
    private Integer levelNumber;

            /**
            * 不可预支提示语
            */
    private String noAdvanceHint;

            /**
            * 不可提现提示语
            */
    private String noWithdrawalHint;

            /**
            * 油比例，如0.01
            */
    private Float oilScale;

            /**
            * 付款期限
            */
    private Integer payPeriod;

            /**
            * 付款期限日
            */
    private Integer payPeriodDay;

            /**
            * 帐期天数
            */
    private Integer paymentDay;

            /**
            * 会员名称
            */
    private String ratingName;

            /**
            * 回单期限
            */
    private Integer receiptPeriod;

            /**
            * 回单期限日
            */
    private Integer receiptPeriodDay;

            /**
            * 报账模式返利比例
            */
    private Float rtnAmtScaleBz;

            /**
            * 承包模式返利比例
            */
    private Float rtnAmtScaleCb;

            /**
            * 智能模式返利比例
            */
    private Float rtnAmtScaleZn;

            /**
            * 结算方式 1-预付全款，2-预付+尾款账期，3-预付+尾款月结
            */
    private Integer settleType;

            /**
            * 状态1 : 可用  0 : 不可用
            */
    private Integer state;

            /**
            * 租户编号
            */
    private Long tenantId;

            /**
            * 每次提现金额
            */
    private Long withdrawalAmount;

            /**
            * 每天提现次数
            */
    private Integer withdrawalNum;



    @TableField(exist = false)
    private List<CreditRatingRuleFee> feeList;

}
