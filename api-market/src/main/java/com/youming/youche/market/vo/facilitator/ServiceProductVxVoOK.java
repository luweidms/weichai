package com.youming.youche.market.vo.facilitator;

import lombok.Data;

import java.io.Serializable;

@Data
public class ServiceProductVxVoOK implements Serializable {

    private Long curOilPrice;

    /**
     * 车队名称
     */
    private String tenantName;
    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 固定价结算
     */
    private Long fixedBalance;

    /**
     * 浮动价结算
     */
    private String floatBalance;

    /**
     * 固定价结算(开票)
     */
    private Long fixedBalanceBill;

    /**
     * 浮动价结算(开票)
     */
    private String floatBalanceBill;


    /**
     * 是否有开票能力（1.有，2.无）
     */
    private Integer isBillAbility;

    /**
     * 关系主键
     */
    private Long relId;

    /**
     * 站点编号
     */
    private Long productId;

    /**
     * 创建租户ID
     */
    private Long tenantId;


    /**
     * 油费单价
     */
    private Long oilPrice;


    /**
     * 现场价 1是 0 不是
     */
    private Integer localeBalanceState;

    private String localeBalanceStateName;


    /**
     * 合作状态 1：合作中  2：已解约
     */
    private Integer cooperationState;


    private String cooperationStateName;


    /**
     * 账期
     */
    private Integer paymentDays;


    /**
     * 结算方式，1账期，2月结
     */
    private Integer balanceType;

    private String balanceTypeName;
    /**
     * '账期结算月份'
     */
    private Integer paymentMonth;


    /**
     * 授信金额
     */
    private Long quotaAmt;


    /**
     * 已使用授信金额
     */
    private Long useQuotaAmt;

    /**
     * 服务商类型（1.油站、2.维修、3.etc供应商）
     */
    private Integer serviceType;

    private String serviceTypeName;
}
