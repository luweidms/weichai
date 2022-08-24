package com.youming.youche.table.dto.workbench;

import com.youming.youche.finance.commons.util.CommonUtil;
import com.youming.youche.table.domain.workbench.BossWorkbenchCustomerInfo;
import com.youming.youche.table.domain.workbench.BossWorkbenchDayInfo;
import com.youming.youche.table.domain.workbench.BossWorkbenchInfo;
import com.youming.youche.table.domain.workbench.BossWorkbenchMonthInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/5/9 10:50
 */
@Data
public class BossWorkbenchInfoDto implements Serializable {

    // 付款审核 + 车辆费用数据
    private BossWorkbenchInfo info;

    // 每日运营报表
    private List<BossWorkbenchDayInfo> dayInfoList;

    // 每月运营报表
    private List<BossWorkbenchMonthInfo> monthInfoList;

    // 客户收入排行
    private List<BossWorkbenchCustomerInfo> customerInfoList;

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

    /**
     * 平台剩余金额
     */
    private Long platformSurplusAmount;

    /**
     * 平台已用金额
     */
    private Long platformUsedAmount;

    /**
     * 剩余金额
     */
    private Long surplusAmount;

    /**
     * 油账户已用金额
     */
    private Long oilUsedAmount;

    /**
     * 油账户剩余金额
     */
    private Long oilSurplusAmount;

    /**
     * 应收账户 已收金额
     */
    private Long receivableReceivedAmount;

    /**
     * 应收账户 剩余金额
     */
    private Long receivableSurplusAmount;

    /**
     * 应付账户 已付金额
     */
    private Long payablePaidAmount;

    /**
     * 应付账户 已付金额
     */
    private Long payableSurplusAmount;

    private BigDecimal platformSurplusAmountDouble;

    private BigDecimal platformUsedAmountDouble;

    private BigDecimal surplusAmountDouble;

    private BigDecimal oilUsedAmountDouble;

    private BigDecimal oilSurplusAmountDouble;

    private BigDecimal receivableReceivedAmountDouble;

    private BigDecimal receivableSurplusAmountDouble;

    private BigDecimal payablePaidAmountDouble;

    private BigDecimal payableSurplusAmountDouble;


    public BigDecimal getPlatformSurplusAmountDouble() {
        return CommonUtil.getDoubleFormatLongMoneyBigDecimal( platformSurplusAmount != null ? platformSurplusAmount : 0L, 2);
    }

    public BigDecimal getPlatformUsedAmountDouble() {
        return CommonUtil.getDoubleFormatLongMoneyBigDecimal(platformUsedAmount != null ? platformUsedAmount : 0L, 2);
    }

    public BigDecimal getSurplusAmountDouble() {
        return CommonUtil.getDoubleFormatLongMoneyBigDecimal(surplusAmount != null ? surplusAmount : 0L, 2);
    }

    public BigDecimal getOilUsedAmountDouble() {
        return CommonUtil.getDoubleFormatLongMoneyBigDecimal(oilUsedAmount != null ? oilUsedAmount : 0L, 2);
    }

    public BigDecimal getOilSurplusAmountDouble() {
        return CommonUtil.getDoubleFormatLongMoneyBigDecimal(oilSurplusAmount != null ? oilSurplusAmount : 0L, 2);
    }

    public BigDecimal getReceivableReceivedAmountDouble() {
        return CommonUtil.getDoubleFormatLongMoneyBigDecimal(receivableReceivedAmount != null ? receivableReceivedAmount : 0L, 2);
    }

    public BigDecimal getReceivableSurplusAmountDouble() {
        return CommonUtil.getDoubleFormatLongMoneyBigDecimal(receivableSurplusAmount != null ? receivableSurplusAmount : 0L, 2);
    }

    public BigDecimal getPayablePaidAmountDouble() {
        return CommonUtil.getDoubleFormatLongMoneyBigDecimal(payablePaidAmount != null ? payablePaidAmount : 0L, 2);
    }

    public BigDecimal getPayableSurplusAmountDouble() {
        return CommonUtil.getDoubleFormatLongMoneyBigDecimal(payableSurplusAmount != null ? payableSurplusAmount : 0L, 2);
    }

    //public List<BossWorkbenchDayInfo> getDayInfoList() {
    //    if (dayInfoList != null) {
    //        for (BossWorkbenchDayInfo workbenchDayInfo : dayInfoList) {
    //            workbenchDayInfo.setBusinessCost(new BigDecimal(workbenchDayInfo.getBusinessCost().longValue() / 10000L));
    //            workbenchDayInfo.setBusinessIncome(new BigDecimal(workbenchDayInfo.getBusinessIncome().longValue() / 10000L));
    //            workbenchDayInfo.setBusinessProfit(new BigDecimal(workbenchDayInfo.getBusinessProfit().longValue() / 10000L));
    //        }
    //
    //        return dayInfoList;
    //    }
    //    return Lists.newArrayList();
    //}
    //
    //public List<BossWorkbenchMonthInfo> getMonthInfoList() {
    //    if (monthInfoList != null) {
    //        for (BossWorkbenchMonthInfo workbenchMonthInfo : monthInfoList) {
    //            workbenchMonthInfo.setBusinessCost(new BigDecimal(workbenchMonthInfo.getBusinessCost().longValue() / 10000L));
    //            workbenchMonthInfo.setBusinessIncome(new BigDecimal(workbenchMonthInfo.getBusinessIncome().longValue() / 10000L));
    //            workbenchMonthInfo.setBusinessProfit(new BigDecimal(workbenchMonthInfo.getBusinessProfit().longValue() / 10000L));
    //        }
    //
    //        return monthInfoList;
    //    }
    //    return Lists.newArrayList();
    //}
}
