package com.youming.youche.finance.vo;

import com.youming.youche.finance.commons.util.CommonUtil;
import com.youming.youche.finance.domain.AccountStatement;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/4/12
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class AccountStatementOutVO extends AccountStatement implements Serializable {
    private Double carTotalFeeDouble;//车辆总费用
    private Double orderTotalFeeDouble;//订单总费用
    private Double exceptionInDouble;//异常补偿
    private Double exceptionOutDouble;//异常扣减
    private Double timePenaltyDouble;//时效罚款
    private Double oilTurnCashDouble;//油转现
    private Double etcTurnCashDouble;//ETC转现
    private Double paidFeeDouble;//已付总金额
    private Double noPayFeeDouble;//未付运费
    private Double oilCardDepositDouble;//订单油卡未退还押金
    private Double marginBalanceDouble;//账户未到期金额
    private Double settlementAmountDouble;//结算金额
    private int opType;//1对账单 2收到对账单

    private String deductionTypeName;//费用扣取方式：1尾款扣除，2现金收取

    /** 这里stateName 以【创建账单状态】即括号外面的中文进行翻译，括号内的要用【发送账单状态】即 ACCOUNT_STATEMENT_STATE_SEND 在业务代码中进行翻译 **/
    private String stateName;//状态：1未发送，2确认中(待确认)，3已确认(结算中)，4已结算，5被驳回(已驳回)

    private String verificationStateName;//核销状态：0待核销，1订单尾款，2线上收款，3线下收款


    public Double getCarTotalFeeDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getCarTotalFee() == null ? 0L : getCarTotalFee(), 2);
    }

    public Double getOrderTotalFeeDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getOrderTotalFee() == null ? 0L : getOrderTotalFee(), 2);
    }

    public Double getExceptionInDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getExceptionIn() == null ? 0L : getExceptionIn(), 2);
    }

    public Double getExceptionOutDouble() {
        return (getExceptionOut() == null || getExceptionOut() == 0L) ? 0L : -CommonUtil.getDoubleFormatLongMoney(getExceptionOut() == null ? 0L : getExceptionOut(), 2);
    }

    public Double getTimePenaltyDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getTimePenalty() == null ? 0L : getTimePenalty(), 2);
    }

    public Double getOilTurnCashDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getOilTurnCash() == null ? 0L : getOilTurnCash(), 2);
    }

    public Double getEtcTurnCashDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getEtcTurnCash() == null ? 0L : getEtcTurnCash(), 2);
    }

    public Double getPaidFeeDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getPaidFee() == null ? 0L : getPaidFee(), 2);
    }

    public Double getNoPayFeeDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getNoPayFee() == null ? 0L : getNoPayFee(), 2);
    }

    public Double getOilCardDepositDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getOilCardDeposit() == null ? 0L : getOilCardDeposit(), 2);
    }

    public Double getMarginBalanceDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getMarginBalance() == null ? 0L : getMarginBalance(), 2);
    }

    public Double getSettlementAmountDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getSettlementAmount() == null ? 0L : getSettlementAmount(), 2);
    }


}
