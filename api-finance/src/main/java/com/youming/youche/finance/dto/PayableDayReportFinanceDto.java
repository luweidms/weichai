package com.youming.youche.finance.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/5/10 14:55
 */
@Data
public class PayableDayReportFinanceDto implements Serializable {

    private Long tenantId;

    /**
     * 业务方
     */
    private Long userId;

    /**
     * 付款类型
     */
    private Long busiId;

    /**
     * 应付时间
     */
    private String createDate;

    /**
     * 业务金额
     */
    private Long txnAmount;

    /**
     * 已付正常
     */
    private Long paidNormalAmount;

    /**
     * 已付逾期
     */
    private Long paidOverdueAmount;

    /**
     * 未付正常
     */
    private Long nopaidNormalAmount;

    /**
     * 未付逾期
     */
    private Long nopaidOverdueAmount;
}
