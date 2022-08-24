package com.youming.youche.table.domain.statistic;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 客户月报
 * </p>
 *
 * @author luwei
 * @since 2022-05-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class StatementCustomerMonth extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 客户id
     */
    private Long customerId;

    /**
     * 客户名称
     */
    private String customerName;

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
     * 创建月份（2022-04）
     */
    private String createMonth;

    /**
     * 车队id
     */
    private Long tenantId;


}
