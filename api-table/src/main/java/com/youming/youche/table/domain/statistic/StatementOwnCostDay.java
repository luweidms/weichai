package com.youming.youche.table.domain.statistic;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 自有成本费用表
 * </p>
 *
 * @author luwei
 * @since 2022-04-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class StatementOwnCostDay extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 报表主键
     */
    private Long statementId;

    /**
     * 油费
     */
    private BigDecimal oilFee;

    /**
     * 路桥费
     */
    private BigDecimal tollFee;

    /**
     * 补贴
     */
    private BigDecimal subsidyFee;

    /**
     * 保费
     */
    private BigDecimal insuranceFee;

    /**
     * 司机借支
     */
    private BigDecimal driverCreditFee;

    /**
     * 司机报销
     */
    private BigDecimal driverExpenseFee;

    /**
     * 维修费
     */
    private BigDecimal maintenanceFee;

    /**
     * 保养费
     */
    private BigDecimal maintainFee;

    /**
     * 停车费
     */
    private BigDecimal parkingFee;

    /**
     * 杂费
     */
    private BigDecimal miscellaneousFee;

    /**
     * 车辆年审
     */
    private BigDecimal vehicleInspectionFee;

    /**
     * 车辆事故
     */
    private BigDecimal vehicleAccidentFee;

    /**
     * 车辆违章
     */
    private BigDecimal vehicleViolationFee;

    /**
     * 员工借支
     */
    private BigDecimal employeesCreditFee;

    /**
     * 现金
     */
    private BigDecimal cash;

    /**
     * 报表类型1部门明细、2客户明细
     */
    private Integer type;


}
