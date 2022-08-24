package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/5/19
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class SaveOrderVo implements Serializable {
    private Object num;

//    private OrderInfo orderInfo;
    private Integer isNeedBill;
    private Integer orgId;

//    private OrderFee orderfee;
    private Long costPrice;
    private Long prePayCash;
    private Integer prePayEquivalenceCardType;
    private String prePayEquivalenceCardNumber;
    private Long prePayEquivalenceCardAmount;
    private Long afterPayCash;
    private Integer afterPayEquivalenceCardType;
    private Long afterPayEquivalenceCardAmount;
    private String afterPayEquivalenceCardNumber;

//    private OrderGoods orderGoods;
    private String goodsName;
    private String customName;
    /**
     * 公司名称
     */
    private String companyName;
    private Long customUserId;
    private Integer goodsType;


//    private OrderInfoExt orderInfoExt;
//    private OrderFeeExt orderFeeExt;


//    private OrderScheduler orderScheduler;
    private Long sourceId;
    private Integer isUrgent;
    private String dispatcherBill;
    private Long dispatcherId;
    private String dispatcherName;

//    private OrderPaymentDaysInfo incomePaymentDaysInfo;
    private Integer balanceType;
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
     * 收款期限
     */
    private Integer collectionTime;

    private Integer paymentDaysType;

    private String dependTime;

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
