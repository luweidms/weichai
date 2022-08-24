package com.youming.youche.market.dto.etc;

import com.youming.youche.market.domain.etc.CmEtcInfo;
import com.youming.youche.record.common.CommonUtil;
import com.youming.youche.util.DateUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/5/20 11:35
 */
@Data
public class CmEtcInfoOutDto extends CmEtcInfo implements Serializable {

    private Double consumeMoneyDouble;//消费金额
    private Double consumeAfterMoneyDouble;//消费折后金额
    private Double consumeProfitDouble;//利润
    //成本模式
    //扣取车主费用日期
    private String cutPaymentDayString;
    private Long beforPay;//扣费前的用户预支金额(单位分)
    private Long consumeMoneyDetail;


    private Double etcAmountDouble;//扣费金额
    private Double marginBalanceDouble;//扣费前的用户未到期金额
    private Double beforePayDouble;//扣费前的用户预支金额

    private Double etcAmountDeductDouble;//etc账户扣除金额
    private Double withdrawalAmountDeductDouble;//可提现余额扣除金额
    private Double marginBalanceDeductDouble;//即将到期余额扣除金额
    private Double advanceFeeDouble;//预支手续费
    private Double arrearsFeeDouble;//欠款扣除金额
    private Boolean isBillPay;//是否对账单结算（招商车挂靠车）
    private Boolean isShowDetails;//是否显示扣款信息

    public Double getConsumeMoneyDouble() {
        if (getConsumeMoney() != null) {
            setConsumeMoneyDouble(CommonUtil.getDoubleFormatLongMoney(getConsumeMoney(), 2));
        }
        return consumeMoneyDouble;
    }

    public Double getConsumeAfterMoneyDouble() {
        if (getConsumeAfterMoney() != null) {
            setConsumeAfterMoneyDouble(CommonUtil.getDoubleFormatLongMoney(getConsumeAfterMoney(), 2));
        }
        return consumeAfterMoneyDouble;
    }

    public Double getConsumeProfitDouble() {
        if (getConsumeProfit() != null) {
            setConsumeProfitDouble(CommonUtil.getDoubleFormatLongMoney(getConsumeProfit(), 2));
        }
        return consumeProfitDouble;
    }

    public Double getEtcAmountDouble() {
        if (getEtcAmount() != null) {
            setEtcAmountDouble(CommonUtil.getDoubleFormatLongMoney(getEtcAmount(), 2));
        }
        return etcAmountDouble;
    }

    public Double getMarginBalanceDouble() {
        if (getMarginBalance() != null) {
            setMarginBalanceDouble(CommonUtil.getDoubleFormatLongMoney(getMarginBalance(), 2));
        }
        return marginBalanceDouble;
    }

    public Double getBeforePayDouble() {
        if (getBeforePay() != null) {
            setBeforePayDouble(CommonUtil.getDoubleFormatLongMoney(getBeforePay(), 2));
        }
        return beforePayDouble;
    }

    public Double getEtcAmountDeductDouble() {
        if (getEtcAmountDeduct() != null) {
            setEtcAmountDeductDouble(CommonUtil.getDoubleFormatLongMoney(getEtcAmountDeduct(), 2));
        }
        return etcAmountDeductDouble;
    }

    public Double getWithdrawalAmountDeductDouble() {
        if (getWithdrawalAmountDeduct() != null) {
            setWithdrawalAmountDeductDouble(CommonUtil.getDoubleFormatLongMoney(getWithdrawalAmountDeduct(), 2));
        }
        return withdrawalAmountDeductDouble;
    }

    public Double getMarginBalanceDeductDouble() {
        if (getMarginBalanceDeduct() != null) {
            setMarginBalanceDeductDouble(CommonUtil.getDoubleFormatLongMoney(getMarginBalanceDeduct(), 2));
        }
        return marginBalanceDeductDouble;
    }

    public Double getAdvanceFeeDouble() {
        if (getAdvanceFee() != null) {
            setAdvanceFeeDouble(CommonUtil.getDoubleFormatLongMoney(getAdvanceFee(), 2));
        }
        return advanceFeeDouble;
    }

    public Double getArrearsFeeDouble() {
        if (getArrearsFee() != null) {
            setArrearsFeeDouble(CommonUtil.getDoubleFormatLongMoney(getArrearsFee(), 2));
        }
        return arrearsFeeDouble;
    }

    public String getCutPaymentDayString() {
        if (getCutPaymentDay() != null) {
            setCutPaymentDayString(DateUtil.formatLocalDateTime(getCutPaymentDay(), DateUtil.DATE_FORMAT));
        }
        return cutPaymentDayString;
    }
}
