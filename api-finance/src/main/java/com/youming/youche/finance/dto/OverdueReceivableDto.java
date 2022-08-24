package com.youming.youche.finance.dto;

import com.youming.youche.finance.commons.util.CommonUtil;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OverdueReceivableDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long flowId;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 目的地市
     */
    private Integer desRegion;

    private String desRegionName;

    /**
     * 起始地市
     */
    private Integer sourceRegion;

    private String sourceRegionName;


    /**
     * 结算方式
     */
    private Integer balanceType;

    /**
     * 收入（分）
     */
    private Long txnAmt;

    private double txnAmtDouble;

    /**
     * 是否确定收款0 否1是
     */
    private Integer payConfirm;

    /**
     * 靠台时间
     */
    private String dependTime;

    /**
     * 付款方名称
     */
    private String name;

    /**
     * 核销金额（分）
     */
    private Long paid;

    private Double paidDouble;

    private String createTime;


    private String updateTime;

    private Integer collectionMonth;

    private Integer collectionDay;

    private Integer collectionTime;

    private String carDependDate; // 靠台时间
    /**
     * 业务类型1、订单收入，2、司机借支，3，员工借支
     */
    private Integer type;

    /**
     * 业务编号
     */
    private String businessNumber;

    /**
     * 业务名称
     */
    private String stateName;


    private String payConfirmName;

    /**
     * 状态名称
     */
    private String stateNames;

    /**
     * 应收日期
     * 预付全款——》当天时间
     * 预付加尾款帐期--》回单审核通过后+收款期限 -- 审核通过后的订单修改时间 审核不通过的数据不统计 订单已完成 orderState == 14
     * 预付加尾款月结--》订单靠台月+收款期限 -- 订单靠台后，取靠台时间，靠台状态 orderState == 7（待装货） > 7
     */
    private String receivableDate;

    /**
     * 待核销
     */
    private Double notWrittenOff;

    public Double getNotWrittenOff() {

        if (getTxnAmt() != null) {
            if (getPaid() != null) {
                return CommonUtil.getDoubleFormatLongMoney(getTxnAmt() - getPaid(), 2);
            } else {
                return CommonUtil.getDoubleFormatLongMoney(getTxnAmt(), 2);
            }
        } else {
            return 0.00;
        }

    }

    /**
     * 分转元
     */
    public double getTxnAmtDouble() {
        if (txnAmt != null) {
            setTxnAmtDouble(CommonUtil.getDoubleFormatLongMoney(txnAmt, 2));
        }
        return txnAmtDouble;
    }

    public Double getPaidDouble() {
        if (paid != null) {
            setPaidDouble(CommonUtil.getDoubleFormatLongMoney(paid, 2));
        }
        return paidDouble;
    }

    /**
     * 线路名称
     */
    private String sourceName;

    private Long userId;

    /**
     * 车队小程序时间展示
     */
    private String createTimeOrb;

}
