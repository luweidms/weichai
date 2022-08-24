package com.youming.youche.order.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
* <p>
    * 
    * </p>
* @author qyf
* @since 2022-03-19
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OrderPaymentDaysInfoH extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 结算方式 枚举:balance_type
     */
    private Integer balanceType;
    /**
     * 收款日
     */
    private Integer collectionDay;
    /**
     * 收款期限月
     */
    private Integer collectionMonth;
    /**
     * 收款期限（天）
     */
    private Integer collectionTime;
    /**
     * 创建时间
     */
    private LocalDateTime createDate;
    /**
     * 补贴结束时间
     */
    private LocalDateTime endDate;
    /**
     * 开票日
     */
    private Integer invoiceDay;
    /**
     * 开票月份
     */
    private Integer invoiceMonth;
    /**
     * 开票期限（天）
     */
    private Integer invoiceTime;
    /**
     * 是否修改
     */
    private Integer isUpdate;
    /**
     * 操作时间
     */
    private LocalDateTime opDate;
    /**
     * 操作员ID
     */
    private Long opId;
    /**
     * 订单id
     */
    private Long orderId;
    /**
     * 账期类型:1 成本 2 收入
     */
    private Integer paymentDaysType;
    /**
     * 回单日
     */
    private Integer reciveDay;
    /**
     * 回单期限月
     */
    private Integer reciveMonth;
    /**
     * 回单期限（天）
     */
    private Integer reciveTime;
    /**
     * 对账日
     */
    private Integer reconciliationDay;
    /**
     * 对账期限月
     */
    private Integer reconciliationMonth;
    /**
     * 对账期限
     */
    private Integer reconciliationTime;
    /**
     * 车队id
     */
    private Long tenantId;


}
