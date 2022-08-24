package com.youming.youche.finance.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CmSalaryVO implements Serializable {

    private static final long serialVersionUID = -2091976793303817142L;


    private String orderId;
    /**
     * 结算月份
     */
    private String settleMonth;

    /**
     * 司机姓名
     */
    private String carDriverName;
    /**
     * 司机手机号码
     */
    private String carDriverPhone;

    private String verificationSalary;//结算金额
//    /**
//     * 订单数
//     */
//    private Integer orderNum;
//    /**
//     * 月里程
//     */
//    private Long monthMileage;
//    /**
//     * 节油奖
//     */
//    private Long saveOilBonus;
//    /**
//     * 已发补贴
//     */
//    private Long answerSubsidyFee;
//    /**
//     * 上月欠款
//     */
//    private Long lastMonthDebt;
//    /**
//     * 基本工资
//     */
//    private Long basicSalaryFee;
//    /**
//     * 申诉数量
//     */
//    private Integer complainCount;
//    /**
//     * 状态
//     */
//    private Integer state;
//    /**
//     * 实发工资
//     */
//    private Long realSalaryFee;
//    /**
//     * 已结算工资
//     */
//    private Long paidSalaryFee;
//    /**
//     * 结算时间
//     */
//    private LocalDateTime checkDate;
    /**失败原因*/
    private String reasonFailure;
}
