package com.youming.youche.finance.dto.ac;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zengwen
 * @date 2022/4/18 16:13
 */
@Data
public class CmSalaryInfoNewQueryDto implements Serializable {

    /**
     * 已发补贴
     */
    private Long answerSubsidyFee;

    /**
     * 基本工资
     */
    private Long basicSalaryFee;

    /**
     * 司机ID
     */
    private Long carDriverId;

    /**
     * 司机姓名
     */
    private String carDriverName;

    /**
     * 司机手机号码
     */
    private String carDriverPhone;

    /**
     * 渠道类型
     */
    private String channelType;

    /**
     * 结算时间
     */
    private LocalDateTime checkDate;

    /**
     * 申诉数量
     */
    private Long complainCount;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 上月欠款
     */
    private Long lastMonthDebt;

    /**
     * 月里程
     */
    private Long monthMileage;

    /**
     * 修改操作员ID
     */
    private Long opId;

    /**
     * 订单数  //TODO:数量采用实时查询
     */
    private Integer orderCount;

    /**
     * 已结算时间
     */
    private LocalDateTime paidDate;

    /**
     * 已结算工资   //TODO:
     */
    private Long paidSalaryFee;

    /**
     * 实发工资
     */
    private Long realSalaryFee;

    /**
     * 备注
     */
    private String remark;

    /**
     * 附件
     */
    private String salaryFiles;

    /**
     * 节油奖
     */
    private Long saveOilBonus;

    /**
     * 工资单发送时间
     */
    private LocalDateTime sendDate;

    /**
     * 结算月份（yyyy-MM）
     */
    private String settleMonth;

    /**
     * 状态 0待发送、2待确认、3已确认、4部分计算、5已结算
     */
    private Integer state;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 趟数工资
     */
    private Long tripPayFee;

    /**
     * 未核销借支
     */
    private Long unwrittenLoanFee;

    /**
     * 更新时间
     */
    private LocalDateTime updateDate;

    /**
     * 修改操作员ID
     */
    private Long updateOpId;

    /**
     * 用户类型
     */
    private Integer userType;


    private String fieldExt0;

    private String fieldExt1;

    private String fieldExt10;

    private String fieldExt11;

    private String fieldExt12;

    private String fieldExt13;

    private String fieldExt14;

    private String fieldExt15;

    private String fieldExt16;

    private String fieldExt17;

    private String fieldExt18;

    private String fieldExt19;

    private String fieldExt2;

    private String fieldExt20;

    private String fieldExt21;

    private String fieldExt22;

    private String fieldExt23;

    private String fieldExt24;

    private String fieldExt25;

    private String fieldExt26;

    private String fieldExt27;

    private String fieldExt28;

    private String fieldExt29;

    private String fieldExt3;

    private String fieldExt4;

    private String fieldExt5;

    private String fieldExt6;

    private String fieldExt7;

    private String fieldExt8;

    private String fieldExt9;

    private String opName;

    private Long salaryId;

    /**
     * 补贴金额
     */
    private Double answerSubsidyFeeDouble;

    /**
     * 上月欠款
     */
    private Double lastMonthDebtDouble;

    /**
     * 基本工资
     */
    private Double basicSalaryFeeDouble;

    /**
     * 月里程
     */
    private Double monthMileageStr;

    /**
     * 节油奖
     */
    private Double saveOilBonusDouble;

    /**
     * 躺数工资
     */
    private Double tripPayFeeDouble;

    /**
     * 未核销金额
     */
    private Double unwrittenLoanFeeDouble;

    /**
     * 已结算工资
     */
    private Double paidSalaryFeeDouble;

    /**
     * 实发工资
     */
    private Double realSalaryFeeDouble;

    /**
     * 状态 0待发送、2待确认、3已确认、4部分计算、5已结算
     */
    private String stateName;

}
