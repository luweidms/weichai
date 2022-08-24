package com.youming.youche.finance.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 账单表
 *
 * @author hzx
 * @date 2022/2/8 11:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderBillInfo extends BaseDomain {

    /**
     * 账单号
     */
    private String billNumber;

    /**
     * 核销金额
     */
    private Long checkAmount;

    /**
     * 发票号（英文逗号隔开支持多个）
     */
    private String receiptNumber;

    /**
     * 账单状态 （对应静态数据 的 code_type = BILL_STS）
     */
    private Integer billSts;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 创建人名称
     */
    private String creatorName;

    /**
     * 创建时间
     */
//    private String createDate;

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
    private Date createReceiptDate;

    /**
     * 开票人名称
     */
    private String createReceiptName;

    /**
     * 核销人
     */
    private String checkBillName;

    /**
     * 核销时间
     */
    private Date checkBillDate;

    /**
     * 实收
     */
    private Long realIncome;
}
