package com.youming.youche.order.vo;

import com.youming.youche.commons.base.BaseDomain;
import com.youming.youche.order.domain.CreditRatingRule;
import com.youming.youche.order.domain.CreditRatingRuleFee;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
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
    public class CreditRatingRuleVo extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 预付款总计比例
     */
    private Float advanceCharge;

    /**
     * 预付款总计金额
     */
    private Float advanceChargeValue;

    /**
     * 预付现金比例
     */
    private Float advanceCash ;

    /**
     * 预付现金金额
     */
    private Float advanceCashValue;

    /**
     * 到付金额比例
     */
    private Float arrivalPayment ;

    /**
     * 到付金额
     */
    private Float arrivalPaymentValue;

    /**
     * 尾款总金额比例
     */
    private Float balancePayment ;

    /**
     * 尾款总金额
     */
    private Float balancePaymentValue;

    /**
     * 尾款里的现金
     */
    private Float balanceValue;

    /**
     * 会员类型：1社会车、2招商车、3自有车、4、个体户、5、外调合同车
     */
    private Integer carUserType;

    /**
     * ETC比例，如0.01
     */
    private Float etcScale;
    /**
     * ETC金额
     */
    private Float etcScaleValue;

    /**
     * 保费金额
     */
    private Long insuranceFee;

    /**
     * 油比例，如0.01
     */
    private Float oilScale;

    /**
     * 油金额
     */
    private Float oilScaleValue;

    /**
     * 招商指导价格（对应单位分） 承包价
     */
    private Float guideMerchant;
    /**
     * 社会指导价格（对应单位分） 拦标价
     */
    private Double guidePrice;

    /**
     * 结算方式 1-预付全款，2-预付+尾款账期，3-预付+尾款月结
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

}
