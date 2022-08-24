package com.youming.youche.finance.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 订单收入账期表
 *
 * @author Terry
 * @since 2021-11-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderPaymentDaysInfo extends BaseDomain {
    /**
     * 主键
     */
    private Long id;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 结算方式 枚举:BALANCE_TYPE
     */
    private Integer balanceType;

    /**
     * 账期类型:1 成本 2 收入
     */
    private Integer paymentDaysType;

    /**
     * 回单期限
     */
    private Integer reciveTime;

    /**
     * 开票期限
     */
    private Integer invoiceTime;

    /**
     * 收款期限
     */
    private Integer collectionTime;

    /**
     * 对账期限
     */
    private Integer reconciliationTime;

    /**
     * 回单月份
     */
    private Integer reciveMonth;

    /**
     * 开票月份
     */
    private Integer invoiceMonth;

    /**
     * 收款月份
     */
    private Integer collectionMonth;

    /**
     * 对账月份
     */
    private Integer reconciliationMonth;

    /**
     * 回单日
     */
    private Integer reciveDay;

    /**
     * 开票日
     */
    private Integer invoiceDay;

    /**
     * 收款日
     */
    private Integer collectionDay;

    /**
     * 对账日
     */
    private Integer reconciliationDay;

    /**
     * 操作时间
     */
    private LocalDateTime opDate;

    /**
     * 操作人
     */
    private Long opId;

    /**
     * 租户ID
     */
    private Long tenantId;

}
