package com.youming.youche.order.domain.order;

    import java.time.LocalDateTime;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 订单费用历史表
    * </p>
* @author chenzhe
* @since 2022-03-21
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OrderFeeH extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 订单编号
            */
    private Long orderId;

            /**
            * 成本价
            */
    private Long costPrice;

            /**
            * 核销时间
            */
    private LocalDateTime financeDate;

            /**
            * 迟到费用
            */
    private Long lateFee;

            /**
            * 车辆归属枚举(对应 静态数据字段  capital_channel)
            */
    private String vehicleAffiliation;

            /**
            * 估算金额
            */
    private Long preAmount;

            /**
            * 保费
            */
    private Long insuranceFee;

            /**
            * 尾款状态
            */
    private Integer finalFeeFlag;

            /**
            * 收入异常总费用
            */
    private Long incomeExceptionFee;

            /**
            * 预付款时间
            */
    private LocalDateTime preAmountTime;

            /**
            * 尾款实际支付费用
            */
    private Long finalFeePay;

            /**
            * 已打款状态
            */
    private Integer payoutStatus;

            /**
            * 现付现金金额
            */
    private Long prePayCash;

            /**
            * 后付现金金额
            */
    private Long afterPayCash;

            /**
            * 现付等值卡金额
            */
    private Long prePayEquivalenceCardAmount;

            /**
            * 后付等值卡金额
            */
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

            /**
            * 现付等值卡卡号
            */
    private String prePayEquivalenceCardNumber;

            /**
            * 后付等值卡卡号
            */
    private String afterPayEquivalenceCardNumber;

            /**
            * 申诉金额
            */
    private Long appealFee;

            /**
            * 未模拟提现实体油费
            */
    private Long noWithdrawalEntityFee;

            /**
            * 未核销实体油费
            */
    private Long noVerificateEntityFee;

            /**
            * 是否通过修改订单添加过实体油 [0:否、1:是]
            */
    private Integer hasIncrEntityFeeFlag;

            /**
            * 总费用
            */
    private Long totalFee;

            /**
            * 1待核销；2已核销；3已撤销
            */
    private Integer verificationState;

            /**
            * 核销时间
            */
    private LocalDateTime verificationDate;

            /**
            * 收款状态,0 未收 1 已收(顺序:现付现金,后付现金,现付等值卡,后付等值卡)
            */
    private String receiptsSts;

            /**
            * 指导价格
            */
    private Long guidePrice;

            /**
            * 操作员ID
            */
    private Long opId;

            /**
            * 租户id
            */
    private Long tenantId;

            /**
            * 修改数据的操作人id
            */
    private Long updateOpId;

            /**
            * 预付总计金额
            */
    private Long preTotalFee;

            /**
            * 预付现金金额
            */
    private Long preCashFee;

            /**
            * 预付虚拟油卡金额
            */
    private Long preOilVirtualFee;

            /**
            * 预付油卡金额
            */
    private Long preOilFee;

            /**
            * 预付ETC金额
            */
    private Long preEtcFee;

            /**
            * 尾款金额
            */
    private Long finalFee;

            /**
            * 收入信息 单位
            */
    private Integer priceEnum;

            /**
            * 成本异常总费用
            */
    private Long costExceptionFee;

            /**
            * 预付总计上限比例标准
            */
    private Long preTotalScaleStandard;

            /**
            * 预付现金比例标准
            */
    private Long preCashScaleStandard;

            /**
            * 预付虚拟油卡比例标准
            */
    private Long preOilVirtualScaleStandard;

            /**
            * 预付油卡比例标准
            */
    private Long preOilScaleStandard;

            /**
            * 预付ETC比例标准
            */
    private Long preEtcScaleStandard;

            /**
            * 预付总计上限比例值
            */
    private Long preTotalScale;

            /**
            * 预付现金比例值
            */
    private Long preCashScale;

            /**
            * 预付虚拟油卡比例值
            */
    private Long preOilVirtualScale;

            /**
            * 预付油卡比例值
            */
    private Long preOilScale;

            /**
            * 预付ETC比例值
            */
    private Long preEtcScale;

            /**
            * 预付尾款比例值
            */
    private Long finalScale;

            /**
            * 单价
            */
    private Long priceUnit;

            /**
            * 账户名称
            */
    private String acctName;

            /**
            * 账户编号
            */
    private String acctNo;

            /**
            * 结束时间
            */
    private LocalDateTime endDate;

            /**
            * 异常收入
            */
    private Long exceptionIn;

            /**
            * 异常支出
            */
    private Long exceptionOut;

            /**
            * 到付状态 0 未支付 1 已支付
            */
    private Integer arrivePaymentState;

            /**
            * 到付费用
            */
    private Long arrivePaymentFee;

            /**
            * 到付费用比例
            */
    private Long arrivePaymentFeeScale;

            /**
            * 到付支付时间
            */
    private LocalDateTime arrivePaymentTime;


}
