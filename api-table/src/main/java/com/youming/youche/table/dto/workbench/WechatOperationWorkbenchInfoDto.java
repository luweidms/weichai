package com.youming.youche.table.dto.workbench;

import com.youming.youche.table.domain.workbench.BossWorkbenchDayInfo;
import com.youming.youche.table.domain.workbench.BossWorkbenchMonthInfo;
import com.youming.youche.table.domain.workbench.WechatOperationWorkbenchInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/5/25 10:14
 */
@Data
public class WechatOperationWorkbenchInfoDto implements Serializable {

    // 每日运营报表
    private List<BossWorkbenchDayInfo> dayInfoList;

    // 每月运营报表
    private List<BossWorkbenchMonthInfo> monthInfoList;

    // 运营经营数据
    private WechatOperationWorkbenchInfo wechatOperationWorkbenchInfo;

    private String ownMarginRate;//自有毛利率

    private String diversionMarginRate;//外调毛利率

    private String merchantsMarginRate;//招商毛利率


    /**
     * 自有毛利率 = （自有收入 - 自有成本） / 自有收入
     */
    public String getOwnMarginRate() {

        DecimalFormat df2 = new DecimalFormat("#0.00%");
        df2.setRoundingMode(RoundingMode.HALF_UP);

        this.ownMarginRate = wechatOperationWorkbenchInfo != null && wechatOperationWorkbenchInfo.getOwnIncome() != null && (wechatOperationWorkbenchInfo.getOwnIncome().compareTo(new BigDecimal(0)) != 0) ?
                df2.format(
                (
                            (wechatOperationWorkbenchInfo.getOwnIncome() != null ? Double.valueOf(String.valueOf(wechatOperationWorkbenchInfo.getOwnIncome())) : new Double(0))
                                -
                            (wechatOperationWorkbenchInfo.getOwnCost() != null ? Double.valueOf(String.valueOf(wechatOperationWorkbenchInfo.getOwnCost())) : new Double(0))
                        )
                        /
                        (wechatOperationWorkbenchInfo.getOwnIncome() != null ? Double.valueOf(String.valueOf(wechatOperationWorkbenchInfo.getOwnIncome())) : new Double(0))
        ) : "0.00%";
       return ownMarginRate;
    }

    /**
     * 外调毛利率 = （外调收入 - 外调成本） / 外调收入
     */
    public String getDiversionMarginRate() {

        DecimalFormat df2 = new DecimalFormat("#0.00%");
        df2.setRoundingMode(RoundingMode.HALF_UP);

        this.diversionMarginRate =
                wechatOperationWorkbenchInfo != null && wechatOperationWorkbenchInfo.getDiversionIncome() != null && (wechatOperationWorkbenchInfo.getDiversionIncome().compareTo(new BigDecimal(0)) != 0) ?
                        df2.format(
                ((wechatOperationWorkbenchInfo.getDiversionIncome() != null ? Double.valueOf(String.valueOf(wechatOperationWorkbenchInfo.getDiversionIncome())) : new Double(0))
                        -
                        (wechatOperationWorkbenchInfo.getDiversionCost() != null ? Double.valueOf(String.valueOf(wechatOperationWorkbenchInfo.getDiversionCost())) : new Double(0)))
                        /
                        (wechatOperationWorkbenchInfo.getDiversionIncome() != null ? Double.valueOf(String.valueOf(wechatOperationWorkbenchInfo.getDiversionIncome())) : new Double(0))
        ) : "0.00%";
        return diversionMarginRate;
    }

    /**
     * 招商毛利率 = （招商收入 - 招商成本） / 招商收入
     */
    public String getMerchantsMarginRate() {

        DecimalFormat df2 = new DecimalFormat("#0.00%");
        df2.setRoundingMode(RoundingMode.HALF_UP);

        this.merchantsMarginRate =
                wechatOperationWorkbenchInfo != null && wechatOperationWorkbenchInfo.getMerchantsIncome() != null && (wechatOperationWorkbenchInfo.getMerchantsIncome().compareTo(new BigDecimal(0)) != 0) ?
                        df2.format(
                ((wechatOperationWorkbenchInfo.getMerchantsIncome() != null ? Double.valueOf(String.valueOf(wechatOperationWorkbenchInfo.getMerchantsIncome())) : new Double(0))
                        -
                        (wechatOperationWorkbenchInfo.getMerchantsCost() != null ? Double.valueOf(String.valueOf(wechatOperationWorkbenchInfo.getMerchantsCost())) : new Double(0)))
                        /
                        (wechatOperationWorkbenchInfo.getMerchantsIncome() != null ? Double.valueOf(String.valueOf(wechatOperationWorkbenchInfo.getMerchantsIncome())) : new Double(0))
        ) : "0.00%";
        return merchantsMarginRate;
    }

}
