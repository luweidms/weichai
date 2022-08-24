package com.youming.youche.finance.dto.order;

import com.youming.youche.finance.commons.util.CommonUtil;
import lombok.Data;

/**
 * @author zengwen
 * @date 2022/4/20 15:15
 */
@Data
public class OrderStatementListToDoubleOutDto extends OrderStatementListOutDto{

    private Double totalFeeDouble;//中标价
    private Double totalAmountDouble;//总运费=中标价+异常补偿
    private Double preCashFeeDouble;//预付现金
    private Double preOilFeeDouble;//预付实体油卡
    private Double preOilVirtualFeeDouble;//预付虚拟油
    private Double preEtcFeeDouble;//预付etc
    private Double finalFeeDouble;//尾款
    private Double totalOilFeeDouble;//总油费
    private Double exceptionInDouble;//异常补偿
    private Double exceptionOutDouble;//异常扣减
    private Double finePriceDouble;//时效罚款
    private Double paidFinalDouble;//限制表已付尾款 = 已付尾款=已到期+预支的尾款+已抵扣尾款
    private Double paidEtcDouble;//限制表已付etc
    private Double oilTurnCashDouble;//限制表油转现
    private Double etcTurnCashDouble;//限制表etc转现
    private Double pledgeOilcardFeeDouble;//限制表油卡押金
    private Double paidCashDouble;//限制表已付现金
    private Double noPayCashDouble;//限制表未付现金
    private Double noPayFinalDouble;//限制表未付尾款
    private Double noPayAmountDouble;//订单未付金额
    private Double paidAmountDouble;//订单已付金额
    private Double arrivePaymentFeeDouble;//到付款
    private Boolean isTransitLine;//是否有经停城市

    public Double getTotalFeeDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getTotalFee(), 2);
    }

    public Double getTotalAmountDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getTotalAmount(), 2);
    }

    public Double getPreCashFeeDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getPreCashFee(), 2);
    }

    public Double getPreOilFeeDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getPreOilFee(), 2);
    }

    public Double getPreOilVirtualFeeDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getPreOilVirtualFee(), 2);
    }

    public Double getPreEtcFeeDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getPreEtcFee(), 2);
    }

    public Double getFinalFeeDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getFinalFee(), 2);
    }

    public Double getTotalOilFeeDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getTotalOilFee(), 2);
    }

    public Double getExceptionInDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getExceptionIn(), 2);
    }

    public Double getExceptionOutDouble() {
        return -CommonUtil.getDoubleFormatLongMoney(getExceptionOut(), 2);
    }

    public Double getFinePriceDouble() {
        return -CommonUtil.getDoubleFormatLongMoney(getFinePrice(), 2);
    }

    public Double getPaidFinalDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getPaidFinal(), 2);
    }

    public Double getPaidEtcDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getPaidEtc(), 2);
    }

    public Double getOilTurnCashDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getOilTurnCash(), 2);
    }

    public Double getEtcTurnCashDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getEtcTurnCash(), 2);
    }

    public Double getPledgeOilcardFeeDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getPledgeOilcardFee(), 2);
    }

    public Double getPaidCashDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getPaidCash(), 2);
    }

    public Double getNoPayCashDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getNoPayCash(), 2);
    }

    public Double getNoPayFinalDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getNoPayFinal(), 2);
    }

    public Double getNoPayAmountDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getNoPayAmount(), 2);
    }

    public Double getPaidAmountDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getPaidAmount(), 2);
    }

    public Double getArrivePaymentFeeDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getArrivePaymentFee(), 2);
    }
}
