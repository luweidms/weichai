package com.youming.youche.finance.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/5/11 13:25
 */
@Data
public class PayableMonthReportFinanceDto implements Serializable {

    private Long tenantId;

    private Long userId;

    private String createDate;

    private Long paidAmount;

    private Long nopaidAmount;
}
