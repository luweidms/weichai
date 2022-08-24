package com.youming.youche.table.dto.receivable;

import com.youming.youche.finance.commons.util.CommonUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/5/11 14:07
 */
@Data
public class PayableMonthReportDto implements Serializable {
    /**
     * 用户Id
     */
    private Long userId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 上上上上上上月以前的已付
     */
    private Long eightPaidAmount;
    /**
     * 上上上上上上月以前的未付
     */
    private Long eightNoPaidAmount;
    /**
     * 上上上上上上月的已付
     */
    private Long servenPaidAmount;
    /**
     * 上上上上上上月的未付
     */
    private Long servenNoPaidAmount;
    /**
     * 上上上上上月的已付
     */
    private Long sixPaidAmount;
    /**
     * 上上上上上月的未付
     */
    private Long sixNoPaidAmount;
    /**
     * 上上上上上月的已付
     */
    private Long fivePaidAmount;
    /**
     * 上上上上月的未付
     */
    private Long fiveNoPaidAmount;
    /**
     * 上上上月的已付
     */
    private Long forePaidAmount;
    /**
     * 上上上月的未付
     */
    private Long foreNoPaidAmount;
    /**
     * 上上月的已付
     */
    private Long threePaidAmount;
    /**
     * 上上月的未付
     */
    private Long threeNoPaidAmount;
    /**
     * 上月的已付
     */
    private Long twoPaidAmount;
    /**
     * 上月的未付
     */
    private Long twoNoPaidAmount;
    /**
     * 本月的已付
     */
    private Long firstPaidAmount;
    /**
     * 本月的未付
     */
    private Long firstNoPaidAmount;
    /**
     * 合计
     */
    private Long sumAmount;
    /**
     * 上上上上上上月以前的已付
     */
    private Double eightPaidAmountDouble;
    /**
     * 上上上上上上月以前的未付
     */
    private Double eightNoPaidAmountDouble;
    /**
     * 上上上上上上月的已付
     */
    private Double servenPaidAmountDouble;
    /**
     * 上上上上上上月的未付
     */
    private Double servenNoPaidAmountDouble;
    /**
     * 上上上上上月的已付
     */
    private Double sixPaidAmountDouble;
    /**
     * 上上上上上月的未付
     */
    private Double sixNoPaidAmountDouble;
    /**
     * 上上上上月的已付
     */
    private Double fivePaidAmountDouble;
    /**
     * 上上上上月的未付
     */
    private Double fiveNoPaidAmountDouble;
    /**
     * 上上上月的已付
     */
    private Double forePaidAmountDouble;
    /**
     * 上上上月的未付
     */
    private Double foreNoPaidAmountDouble;
    /**
     * 上上月的已付
     */
    private Double threePaidAmountDouble;
    /**
     * 上上月的未付
     */
    private Double threeNoPaidAmountDouble;
    /**
     * 上月的已付
     */
    private Double twoPaidAmountDouble;
    /**
     * 上月的未付
     */
    private Double twoNoPaidAmountDouble;
    /**
     * 本月的已付
     */
    private Double firstPaidAmountDouble;
    /**
     * 本月的未付
     */
    private Double firstNoPaidAmountDouble;
    /**
     * 合计
     */
    private Double sumAmountDouble;

    public Double getEightPaidAmountDouble() {
        if (eightPaidAmount != null) {
            setEightPaidAmountDouble(CommonUtil.getDoubleFormatLongMoney(eightPaidAmount, 2));
        }
        return eightPaidAmountDouble;
    }

    public Double getEightNoPaidAmountDouble() {
        if (eightNoPaidAmount != null) {
            setEightNoPaidAmountDouble(CommonUtil.getDoubleFormatLongMoney(eightNoPaidAmount, 2));
        }
        return eightNoPaidAmountDouble;
    }

    public Double getServenPaidAmountDouble() {
        if (servenPaidAmount != null) {
            setServenPaidAmountDouble(CommonUtil.getDoubleFormatLongMoney(servenPaidAmount, 2));
        }
        return servenPaidAmountDouble;
    }

    public Double getServenNoPaidAmountDouble() {
        if (servenNoPaidAmount != null) {
            setServenNoPaidAmountDouble(CommonUtil.getDoubleFormatLongMoney(servenNoPaidAmount, 2));
        }
        return servenNoPaidAmountDouble;
    }

    public Double getSixPaidAmountDouble() {
        if (sixPaidAmount != null) {
            setSixPaidAmountDouble(CommonUtil.getDoubleFormatLongMoney(sixPaidAmount, 2));
        }
        return sixPaidAmountDouble;
    }

    public Double getSixNoPaidAmountDouble() {
        if (sixNoPaidAmount != null) {
            setSixNoPaidAmountDouble(CommonUtil.getDoubleFormatLongMoney(sixNoPaidAmount, 2));
        }
        return sixNoPaidAmountDouble;
    }

    public Double getFivePaidAmountDouble() {
        if (fivePaidAmount != null) {
            setFivePaidAmountDouble(CommonUtil.getDoubleFormatLongMoney(fivePaidAmount, 2));
        }
        return fivePaidAmountDouble;
    }

    public Double getFiveNoPaidAmountDouble() {
        if (fiveNoPaidAmount != null) {
            setFiveNoPaidAmountDouble(CommonUtil.getDoubleFormatLongMoney(fiveNoPaidAmount, 2));
        }
        return fiveNoPaidAmountDouble;
    }

    public Double getForePaidAmountDouble() {
        if (forePaidAmount != null) {
            setForePaidAmountDouble(CommonUtil.getDoubleFormatLongMoney(forePaidAmount, 2));
        }
        return forePaidAmountDouble;
    }

    public Double getForeNoPaidAmountDouble() {
        if (foreNoPaidAmount != null) {
            setForeNoPaidAmountDouble(CommonUtil.getDoubleFormatLongMoney(foreNoPaidAmount, 2));
        }
        return foreNoPaidAmountDouble;
    }

    public Double getThreePaidAmountDouble() {
        if (threePaidAmount != null) {
            setThreePaidAmountDouble(CommonUtil.getDoubleFormatLongMoney(threePaidAmount, 2));
        }
        return threePaidAmountDouble;
    }

    public Double getThreeNoPaidAmountDouble() {
        if (threeNoPaidAmount != null) {
            setThreeNoPaidAmountDouble(CommonUtil.getDoubleFormatLongMoney(threeNoPaidAmount, 2));
        }
        return threeNoPaidAmountDouble;
    }

    public Double getTwoPaidAmountDouble() {
        if (twoPaidAmount != null) {
            setTwoPaidAmountDouble(CommonUtil.getDoubleFormatLongMoney(twoPaidAmount, 2));
        }
        return twoPaidAmountDouble;
    }

    public Double getTwoNoPaidAmountDouble() {
        if (twoNoPaidAmount != null) {
            setTwoNoPaidAmountDouble(CommonUtil.getDoubleFormatLongMoney(twoNoPaidAmount, 2));
        }
        return twoNoPaidAmountDouble;
    }

    public Double getFirstPaidAmountDouble() {
        if (firstPaidAmount != null) {
            setFirstPaidAmountDouble(CommonUtil.getDoubleFormatLongMoney(firstPaidAmount, 2));
        }
        return firstPaidAmountDouble;
    }

    public Double getFirstNoPaidAmountDouble() {
        if (firstNoPaidAmount != null) {
            setFirstNoPaidAmountDouble(CommonUtil.getDoubleFormatLongMoney(firstNoPaidAmount, 2));
        }
        return firstNoPaidAmountDouble;
    }

    public Double getSumAmountDouble() {
        setSumAmountDouble(
                getTwoNoPaidAmountDouble() + getTwoPaidAmountDouble() +
                        getThreeNoPaidAmountDouble() + getThreePaidAmountDouble() +
                        getForeNoPaidAmountDouble() + getForePaidAmountDouble() +
                        getFiveNoPaidAmountDouble() + getFivePaidAmountDouble() +
                        getSixNoPaidAmountDouble() + getSixPaidAmountDouble() +
                        getServenNoPaidAmountDouble() + getServenPaidAmountDouble() +
                        getEightNoPaidAmountDouble() + getEightPaidAmountDouble()
        );
//        if (sumAmount != null) {
//            setSumAmountDouble(CommonUtil.getDoubleFormatLongMoney(sumAmount, 2));
//        }
        return sumAmountDouble;
    }
}
