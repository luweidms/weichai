package com.youming.youche.finance.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 账单_发票表
 *
 * @author hzx
 * @date 2022/2/8 11:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderBillInvoice extends BaseDomain {

    private Long id;
    /**
     * 发票号
     */
    private String receiptNumber;

    /**
     * 发票金额
     */
    private Long amount;

    /**
     * 账单号
     */
    private String billNumber;

    /**
     * 操作人
     */
    private Long operId;

    /**
     * 操作时间
     */
    private String operDate;

    /**
     * 归属租户
     */
    private Long tenantId;

    /**
     * 开票时间
     */
    private String createReceiptDate;

    /**
     * 开票人名称
     */
    private String createReceiptName;

}
