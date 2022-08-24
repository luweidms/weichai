package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SumQuotaAmtDto implements Serializable {
    private static final long serialVersionUID = 1399800840047295455L;
    private Long totalQuotaAmt;
    /**
     * useQuotaAmt
     */
    private Long totalUseQuotaAmt;
}
