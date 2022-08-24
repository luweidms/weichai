package com.youming.youche.table.domain.statistic;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 部门日报
 * </p>
 *
 * @author luwei
 * @since 2022-04-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class StatementDepartmentDay extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 部门id
     */
    private Long departmentId;

    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 自有收入
     */
    private BigDecimal ownIncome;

    /**
     * 自有成本
     */
    private BigDecimal ownCost;

    /**
     * 自有毛利
     */
    private BigDecimal ownGrossMargin;

    /**
     * 外调收入
     */
    private BigDecimal diversionIncome;

    /**
     * 外调成本
     */
    private BigDecimal diversionCost;

    /**
     * 外调毛利
     */
    private BigDecimal diversionGrossMargin;

    /**
     * 招商收入
     */
    private BigDecimal merchantsIncome;

    /**
     * 招商成本
     */
    private BigDecimal merchantsCost;

    /**
     * 招商毛利
     */
    private BigDecimal merchantsGrossMargin;

    /**
     * 创建日期
     */
    private LocalDate createDate;

    /**
     * 车队id
     */
    private Long tenantId;

    /**
     * 预留字段1
     */
    @TableField("Column1")
    private String column1;

    /**
     * 预留字段2
     */
    @TableField("Column2")
    private String column2;


}
