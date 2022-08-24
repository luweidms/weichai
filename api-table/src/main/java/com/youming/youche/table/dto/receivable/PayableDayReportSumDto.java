package com.youming.youche.table.dto.receivable;

import com.youming.youche.finance.commons.util.CommonUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/5/12 16:30
 */
@Data
public class PayableDayReportSumDto implements Serializable {

    /**
     * 业务金额
     */
    private Long txnAmount;

    /**
     * 已付正常
     */
    private Long paidNormalAmount;

    /**
     * 已付逾期
     */
    private Long paidOverdueAmount;

    /**
     * 未付正常
     */
    private Long nopaidNormalAmount;

    /**
     * 未付逾期
     */
    private Long nopaidOverdueAmount;

    /**
     * 业务金额
     */
    private double txnAmountDouble;

    /**
     * 已付正常
     */
    private double paidNormalAmountDouble;

    /**
     * 已付逾期
     */
    private double paidOverdueAmountDouble;

    /**
     * 未付正常
     */
    private double nopaidNormalAmountDouble;

    /**
     * 未付逾期
     */
    private double nopaidOverdueAmountDouble;


    public double getTxnAmountDouble() {
        if (txnAmount != null) {
            setTxnAmountDouble(CommonUtil.getDoubleFormatLongMoney(txnAmount, 2));
        }
        return txnAmountDouble;
    }


    public double getPaidNormalAmountDouble() {
        if (paidNormalAmount != null) {
            setPaidNormalAmountDouble(CommonUtil.getDoubleFormatLongMoney(paidNormalAmount, 2));
        }
        return paidNormalAmountDouble;
    }

    public double getPaidOverdueAmountDouble() {
        if (paidOverdueAmount != null) {
            setPaidOverdueAmountDouble(CommonUtil.getDoubleFormatLongMoney(paidOverdueAmount, 2));
        }
        return paidOverdueAmountDouble;
    }

    public double getNopaidNormalAmountDouble() {
        if (nopaidNormalAmount != null) {
            setNopaidNormalAmountDouble(CommonUtil.getDoubleFormatLongMoney(nopaidNormalAmount, 2));
        }
        return nopaidNormalAmountDouble;
    }

    public double getNopaidOverdueAmountDouble() {
        if (nopaidOverdueAmount != null) {
            setNopaidOverdueAmountDouble(CommonUtil.getDoubleFormatLongMoney(nopaidOverdueAmount, 2));
        }
        return nopaidOverdueAmountDouble;
    }
}
