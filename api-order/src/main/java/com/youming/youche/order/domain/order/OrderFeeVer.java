package com.youming.youche.order.domain.order;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderFeeVer extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 账户名称
     */
    private String acctName;
    /**
     *账户编号
     */
    private String acctNo;
    /**
     *后付结算类型
     */
    private Integer afterPayAcctType;
    /**
     *后付现金金额
     */
    private Long afterPayCash;
    /**
     *后付等值卡类型
     */
    private Long afterPayEquivalenceCardAmount;
    /**
     *后付等值卡卡号
     */
    private String afterPayEquivalenceCardNumber;
    /**
     *后付等值卡类型
     */
    private Integer afterPayEquivalenceCardType;
    /**
     *申诉金额
     */
    private Long appealFee;
    /**
     *到付费用
     */
    private Long arrivePaymentFee;
    /**
     *到付费用比例
     */
    private Long arrivePaymentFeeScale;
    /**
     *到付状态 0 未支付 1 已支付
     */
    private Integer arrivePaymentState;
    /**
     *到付支付时间
     */
    private LocalDateTime arrivePaymentTime;
    /**
     *成本异常总费用
     */
    private Long costExceptionFee;
    /**
     *订单收入
     */
    private Long costPrice;
    /**
     *异常收入
     */
    private Long exceptionIn;
    /**
     *异常支出
     */
    private Long exceptionOut;
    /**
     *尾款金额
     */
    private Long finalFee;
    /**
     *尾款状态
     */
    private Integer finalFeeFlag;
    /**
     *尾款实际支付费用
     */
    private Long finalFeePay;
    /**
     *预付尾款比例值
     */
    private Long finalScale;
    /**
     *核销时间
     */
    private LocalDateTime financeDate;
    /**
     *指导价格
     */
    private Long guidePrice;
    /**
     *是否通过修改订单添加过实体油 [0:否、1:是]
     */
    private Integer hasIncrEntityFeeFlag;
    /**
     *收入异常总费用
     */
    private Long incomeExceptionFee;
    /**
     *保费
     */
    private Long insuranceFee;
    /**
     *迟到费用
     */
    private Long lateFee;
    /**
     *未核销实体油费
     */
    private Long noVerificateEntityFee;
    /**
     *未模拟提现实体油费
     */
    private Long noWithdrawalEntityFee;
    /**
     *操作员id
     */
    private Long opId;
    /**
     *订单号
     */
    private Long orderId;
    /**
     *已打款状态
     */
    private Integer payoutStatus;
    /**
     *估算金额
     */
    private Long preAmount;
    /**
     *预付款时间
     */
    private LocalDateTime preAmountTime;
    /**
     *预付现金金额
     */
    private Long preCashFee;
    /**
     *预付现金比例值
     */
    private Long preCashScale;
    /**
     *预付现金比例标准
     */
    private Long preCashScaleStandard;
    /**
     *预付ETC金额
     */
    private Long preEtcFee;
    /**
     *预付ETC比例值
     */
    private Long preEtcScale;
    /**
     *预付ETC比例标准
     */
    private Long preEtcScaleStandard;
    /**
     *预付实体油
     */
    private Long preOilFee;
    /**
     *预付油卡比例值
     */
    private Long preOilScale;
    /**
     *预付油卡比例标准
     */
    private Long preOilScaleStandard;
    /**
     *预付虚拟油卡金额
     */
    private Long preOilVirtualFee;
    /**
     *预付虚拟油卡比例值
     */
    private Long preOilVirtualScale;
    /**
     *预付虚拟油卡比例标准
     */
    private Long preOilVirtualScaleStandard;
    /**
     *现付现金金额
     */
    private Long prePayCash;
    /**
     *现付等值卡金额
     */
    private Long prePayEquivalenceCardAmount;
    /**
     *现付等值卡卡号
     */
    private String prePayEquivalenceCardNumber;
    /**
     *现付等值卡类型
     */
    private Integer prePayEquivalenceCardType;
    /**
     *预付总计金额
     */
    private Long preTotalFee;
    /**
     *预付总计上限比例值
     */
    private Long preTotalScale;
    /**
     *预付总计上限比例标准
     */
    private Long preTotalScaleStandard;
    /**
     *收入信息
     */
    private Integer priceEnum;
    /**
     *单价
     */
    private Double priceUnit;
    /**
     *收款状态,0 未收 1 已收(顺序:现付现金,后付现金,现付等值卡,后付等值卡)
     */
    private String receiptsSts;
    /**
     *租户ID
     */
    private Long tenantId;
    /**
     *订单总金额
     */
    private Long totalFee;
    /**
     *修改人
     */
    private Long updateOpId;
    /**
     *资金渠道
     */
    private String vehicleAffiliation;
    /**
     *核销时间
     */
    private LocalDateTime verificationDate;
    /**
     *1待核销；2已核销；3已撤销
     */
    private Integer verificationState;
    /**
     *收入信息
     */
    @TableField(exist = false)
    private String priceEnumName;


}
