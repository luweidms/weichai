package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhouchao on 18/4/9.
 */
@Data
public class OrderIncomeInDto implements Serializable{


    /**
     * costPrice : 100
     * afterPayEquivalenceCardType : -1
     * afterPayAcctType : 1
     * prePayEquivalenceCardType : -1
     * isUrgent : 0
     */

    private String costPrice;
    /**
     * 后付等值卡类型
     */
    private String afterPayEquivalenceCardType;
    private String afterPayAcctType;
    /**
     * 现付等值卡类型
     */
    private String prePayEquivalenceCardType;
    private String isUrgent;
    private String prePayCashDouble;
    private String prePayEquivalenceCardAmountDouble;
    private String afterPayCashDouble;
    private String prePayEquivalenceCardNumber;
    private String afterPayEquivalenceCardNumber;
    private String priceUnitY;
    /**
     * 收入信息 单位
     */
    private String priceEnum;
    private String costPriceY;
    /**
     * 回单期限（天）
     */
    private String reciveTime;
    /**
     * 开票期限（天）
     */
    private String invoiceTime;
    /**
     * 收款期限
     */
    private String collectionTime;


    /**
     * incomeCashFee : 30000
     * incomeOilVirtualFee : 90000
     * incomeOilFee : 0
     * incomeEtcFee : 120000
     * incomeFinalFee : 60000
     * incomeInsuranceFee : 4000
     * incomePaymentDays : 50
     */

    private String incomeCashFee;
    private String incomeOilVirtualFee;
    private String incomeOilFee;
    private String incomeEtcFee;
    private String incomeFinalFee;
    private String incomeArrivePaymentFee;
    private String incomeInsuranceFee;
    private String incomePaymentDays;
    private String afterPayEquivalenceCardAmountDouble;

    
}
