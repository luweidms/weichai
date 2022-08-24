package com.youming.youche.finance.dto;

import com.youming.youche.finance.commons.util.CommonUtil;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author zengwen
 * @date 2022/6/27 17:14
 */
@Data
public class QueryAccountStatementOrdersDto implements Serializable {

    private Long orderId; // 订单号
    private LocalDateTime dependTime; //靠台时间
    private Integer orderState; //订单状态
    private String orderStateName;

    private String companyName;//客户名称
    private String sourceName;//线路名称
    private Long carDriverId; // 司机ID
    private String carDriverMan;//司机名称
    private String carDriverPhone;//司机手机
    private Long payeeUserId;
    private String payee;//收款人名称
    private String payeePhone;//收款人手机
    private Integer isCollection;//是否代收

    private Long totalFee;//中标价
    private Double totalFeeDouble;

    private Long preCashFee;//预付现金
    private Double preCashFeeDouble;

    private Long totalOilFee;//总油费
    private Double totalOilFeeDouble;

    private Long preOilFee;//预付实体油卡
    private Double preOilFeeDouble;

    private Long preOilVirtualFee;//预付虚拟油
    private Double preOilVirtualFeeDouble;

    private Long preEtcFee;//预付etc
    private Double preEtcFeeDouble;

    private Long arrivePaymentFee; //到付款
    private Double arrivePaymentFeeDouble;

    private Long finalFee;//尾款
    private Double finalFeeDouble;
    private Integer fianlSts;//限制表尾款到期状态
    private String fianlStsName;//尾款到期状态名字

    private Long paidFinal;//已付尾款
    private Double paidFinalDouble;

    private Long exceptionIn;//异常补偿
    private Long exceptionOut;//异常扣减
    private Long finePrice;//时效罚款
    private Double exceptionInDouble;
    private Double exceptionOutDouble;
    private Double finePriceDouble;

    private Long totalAmount;//总运费=中标价+异常补偿
    private Double totalAmountDouble;

    private Long paidAmount;//已付
    private Double paidAmountDouble;

    private Long noPayAmount;//订单未付金额
    private Double noPayAmountDouble;

    public Double getTotalFeeDouble() {
        if (totalFee != null) {
            setTotalFeeDouble(CommonUtil.getDoubleFormatLongMoney(totalFee, 2));
        }
        return totalFeeDouble;
    }

    public Double getPreCashFeeDouble() {
        if (preCashFee != null) {
            setPreCashFeeDouble(CommonUtil.getDoubleFormatLongMoney(preCashFee, 2));
        }
        return preCashFeeDouble;
    }

    public Double getTotalOilFeeDouble() {
        if (totalOilFee != null) {
            setTotalOilFeeDouble(CommonUtil.getDoubleFormatLongMoney(totalOilFee, 2));
        }
        return totalOilFeeDouble;
    }

    public Double getPreOilFeeDouble() {
        if (preOilFee != null) {
            setPreOilFeeDouble(CommonUtil.getDoubleFormatLongMoney(preOilFee, 2));
        }
        return preOilFeeDouble;
    }

    public Double getPreOilVirtualFeeDouble() {
        if (preOilVirtualFee != null) {
            setPreOilVirtualFeeDouble(CommonUtil.getDoubleFormatLongMoney(preOilVirtualFee, 2));
        }
        return preOilVirtualFeeDouble;
    }

    public Double getPreEtcFeeDouble() {
        if (preEtcFee != null) {
            setPreEtcFeeDouble(CommonUtil.getDoubleFormatLongMoney(preEtcFee, 2));
        }
        return preEtcFeeDouble;
    }

    public Double getArrivePaymentFeeDouble() {
        if (arrivePaymentFee != null) {
            setArrivePaymentFeeDouble(CommonUtil.getDoubleFormatLongMoney(arrivePaymentFee, 2));
        }
        return arrivePaymentFeeDouble;
    }

    public Double getFinalFeeDouble() {
        if (finalFee != null) {
            setFinalFeeDouble(CommonUtil.getDoubleFormatLongMoney(finalFee, 2));
        }
        return finalFeeDouble;
    }

    public Double getPaidFinalDouble() {
        if (paidFinal != null) {
            setPaidFinalDouble(CommonUtil.getDoubleFormatLongMoney(paidFinal, 2));
        }
        return paidFinalDouble;
    }

    public Double getExceptionInDouble() {
        if (exceptionIn != null) {
            setExceptionInDouble(CommonUtil.getDoubleFormatLongMoney(exceptionIn, 2));
        }
        return exceptionInDouble;
    }

    public Double getExceptionOutDouble() {
        if (exceptionOut != null) {
            setExceptionOutDouble(CommonUtil.getDoubleFormatLongMoney(exceptionOut, 2));
        }
        return exceptionOutDouble;
    }

    public Double getFinePriceDouble() {
        if (finePrice != null) {
            setFinePriceDouble(CommonUtil.getDoubleFormatLongMoney(finePrice, 2));
        }
        return finePriceDouble;
    }

    public Double getTotalAmountDouble() {
        if (totalAmount != null) {
            setTotalAmountDouble(CommonUtil.getDoubleFormatLongMoney(totalAmount, 2));
        }
        return totalAmountDouble;
    }

    public Double getPaidAmountDouble() {
        if (paidAmount != null) {
            setPaidAmountDouble(CommonUtil.getDoubleFormatLongMoney(paidAmount, 2));
        }
        return paidAmountDouble;
    }

    public Double getNoPayAmountDouble() {
        if (noPayAmount != null) {
            setNoPayAmountDouble(CommonUtil.getDoubleFormatLongMoney(noPayAmount, 2));
        }
        return noPayAmountDouble;
    }
}
