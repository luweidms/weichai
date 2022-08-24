package com.youming.youche.table.dto.workbench;

import com.youming.youche.finance.commons.util.CommonUtil;
import com.youming.youche.table.domain.workbench.FinancialWorkbenchInfo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zengwen
 * @date 2022/5/5 16:10
 */
@Data
public class FinancialWorkbenchInfoDto extends FinancialWorkbenchInfo {

    /**
     * 维保数量(待我审)
     */
    private Long maintenanceReviewAmount;

    /**
     * 维保数量(我发起)
     */
    private Long maintenanceMeReviewAmount;

    /**
     * 车辆费用(待我审)
     */
    private Long vehicleCostReviewAmount;

    /**
     * 车辆费用(我发起)
     */
    private Long vehicleCostMeReviewAmount;

    /**
     * 管理费用(待我审)
     */
    private Long manageCostReviewAmount;

    /**
     * 管理费用(我发起)
     */
    private Long manageCostMeReviewAmount;

    /**
     * 费用借支(待我审)
     */
    private Long costBorrowReviewAmount;

    /**
     * 费用借支(我发起)
     */
    private Long costBorrowMeReviewAmount;


    private BigDecimal platformSurplusAmountDouble;

    private BigDecimal platformUsedAmountDouble;

    private BigDecimal surplusAmountDouble;

    private BigDecimal oilUsedAmountDouble;

    private BigDecimal oilSurplusAmountDouble;

    private BigDecimal receivableReceivedAmountDouble;

    private BigDecimal receivableSurplusAmountDouble;

    private BigDecimal payablePaidAmountDouble;

    private BigDecimal payableSurplusAmountDouble;

    private BigDecimal overdueReceivablesAmountDouble;

    private BigDecimal overduePayableAmountDouble;

    private BigDecimal rechargeTodayAmountDouble;

    private BigDecimal pendingPaymentAmountDouble;


    public BigDecimal getPlatformSurplusAmountDouble() {
        return CommonUtil.getDoubleFormatLongMoneyBigDecimal( super.getPlatformSurplusAmount() != null ? super.getPlatformSurplusAmount() : 0L, 2);
    }

    public BigDecimal getPlatformUsedAmountDouble() {
        return CommonUtil.getDoubleFormatLongMoneyBigDecimal(super.getPlatformUsedAmount() != null ? super.getPlatformUsedAmount() : 0L, 2);
    }

    public BigDecimal getSurplusAmountDouble() {
        return CommonUtil.getDoubleFormatLongMoneyBigDecimal(super.getSurplusAmount() != null ? super.getSurplusAmount() : 0L, 2);
    }

    public BigDecimal getOilUsedAmountDouble() {
        return CommonUtil.getDoubleFormatLongMoneyBigDecimal(super.getOilUsedAmount() != null ? super.getOilUsedAmount() : 0L, 2);
    }

    public BigDecimal getOilSurplusAmountDouble() {
        return CommonUtil.getDoubleFormatLongMoneyBigDecimal(super.getOilSurplusAmount() != null ? super.getOilSurplusAmount() : 0L, 2);
    }

    public BigDecimal getReceivableReceivedAmountDouble() {
        return CommonUtil.getDoubleFormatLongMoneyBigDecimal(super.getReceivableReceivedAmount() != null ? super.getReceivableReceivedAmount() : 0L, 2);
    }

    public BigDecimal getReceivableSurplusAmountDouble() {
        return CommonUtil.getDoubleFormatLongMoneyBigDecimal(super.getReceivableSurplusAmount() != null ? super.getReceivableSurplusAmount() : 0L, 2);
    }

    public BigDecimal getPayablePaidAmountDouble() {
        return CommonUtil.getDoubleFormatLongMoneyBigDecimal(super.getPayablePaidAmount() != null ? super.getPayablePaidAmount() : 0L, 2);
    }

    public BigDecimal getPayableSurplusAmountDouble() {
        return CommonUtil.getDoubleFormatLongMoneyBigDecimal(super.getPayableSurplusAmount() != null ? super.getPayableSurplusAmount() : 0L, 2);
    }

    public BigDecimal getOverdueReceivablesAmountDouble() {
        return CommonUtil.getDoubleFormatLongMoneyBigDecimal(super.getOverdueReceivablesAmount() != null ? super.getOverdueReceivablesAmount() : 0L, 2);
    }

    public BigDecimal getOverduePayableAmountDouble() {
        return CommonUtil.getDoubleFormatLongMoneyBigDecimal(super.getOverduePayableAmount() != null ? super.getOverduePayableAmount() : 0L, 2);
    }

    public BigDecimal getRechargeTodayAmountDouble() {
        return CommonUtil.getDoubleFormatLongMoneyBigDecimal(super.getRechargeTodayAmount() != null ? super.getRechargeTodayAmount() : 0L, 2);
    }

    public BigDecimal getPendingPaymentAmountDouble() {
        return CommonUtil.getDoubleFormatLongMoneyBigDecimal(super.getPendingPaymentAmount() != null ? super.getPendingPaymentAmount() : 0L, 2);
    }
}
