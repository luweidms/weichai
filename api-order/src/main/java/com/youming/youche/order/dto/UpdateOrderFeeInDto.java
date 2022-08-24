package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateOrderFeeInDto  implements Serializable {
    private Long costPrice;
    private Long prePayCash;
    private Long afterPayCash;
    private Long prePayEquivalenceCardAmount;
    private Long afterPayEquivalenceCardAmount;
    /**
     * 现付等值卡类型
     */
    private Integer prePayEquivalenceCardType;
    /**
     * 后付等值卡类型
     */
    private Integer afterPayEquivalenceCardType;
    /**
     * 后付结算类型
     */
    private Integer afterPayAcctType;
    private String prePayEquivalenceCardNumber;
    private String afterPayEquivalenceCardNumber;
    private Long totalFee;
    private Long preTotalFee;
    private Long preCashFee;
    private Long preOilVirtualFee;
    private Long preOilFee;
    private Long preEtcFee;
    private Long finalFee;
    /**
     * 收入信息 单位
     */
    private Integer priceEnum;
    /**
     * 单价
     */
    private Double priceUnit;
    private Long preTotalScaleStandard;
    private Long preCashScaleStandard;
    private Long preOilVirtualScaleStandard;
    private Long preOilScaleStandard;
    private Long preEtcScaleStandard;
    private Long preTotalScale;
    private Long preCashScale;
    private Long preOilVirtualScale;
    private Long preOilScale;
    private Long preEtcScale;
    private Long finalScale;
    /**
     *资金渠道
     */
    private String vehicleAffiliation;
    private Long arrivePaymentFee;
    private Long arrivePaymentFeeScale;
    private Long insuranceFee;
}
