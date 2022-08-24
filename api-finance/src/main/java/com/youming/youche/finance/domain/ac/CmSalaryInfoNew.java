package com.youming.youche.finance.domain.ac;

    import java.time.LocalDateTime;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author zengwen
* @since 2022-04-18
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class CmSalaryInfoNew extends BaseDomain {

    private static final long serialVersionUID = 1L;

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
    private Integer complainCount;

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
     * 订单数   //TODO:数量采用实时查询
     */
    private Integer orderCount;

    /**
     * 已结算时间
     */
    private LocalDateTime paidDate;

    /**
     * 已结算工资     //TODO: 外层所有工资+补贴明细结算工资总和
     */
    private Long paidSalaryFee;

    /**
     * 实发工资    //TODO:外层所有金额
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

}
