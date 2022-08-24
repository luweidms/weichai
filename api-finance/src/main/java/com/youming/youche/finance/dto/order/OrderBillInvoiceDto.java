package com.youming.youche.finance.dto.order;

import com.youming.youche.finance.commons.util.CommonUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hzx
 * @date 2022/2/9 17:53
 */
@Data
public class OrderBillInvoiceDto implements Serializable {

    private static final long serialVersionUID = 1L;

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

    private Double amountDouble;

    public Double getAmountDouble() {
        setAmountDouble(getAmount() == null ? CommonUtil.getDoubleFormatLongMoney(new Double(0).longValue(), 2) : CommonUtil.getDoubleFormatLongMoney(getAmount().longValue(), 2));
        return amountDouble;
    }

}
