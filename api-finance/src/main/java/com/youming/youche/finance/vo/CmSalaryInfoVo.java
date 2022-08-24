package com.youming.youche.finance.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CmSalaryInfoVo implements Serializable {

    /**
     * 工资编号
     */
    private Long salaryId;

    /**
     * 结算月份
     */
    private String settleMonth;

    private String relPayFee;
    /**
     * 已结算工资     //TODO: 外层所有工资+补贴明细结算工资总和
     */
    private Long paidSalaryFee;

    private Long tenantId;

    private Integer state;

    private String stsName;

    private String sourceName;
}
