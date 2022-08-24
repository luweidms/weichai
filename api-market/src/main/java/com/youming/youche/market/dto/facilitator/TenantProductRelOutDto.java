package com.youming.youche.market.dto.facilitator;

import lombok.Data;

import java.io.Serializable;
@Data
public class TenantProductRelOutDto implements Serializable {
    private Long productId;
    private String floatBalance;
    private Long fixedBalance;
    private String serviceCharge;
    private String productName;
    private Integer isBillAbility;
    private Long tenantId;

    private String floatBalanceBill;
    private Long fixedBalanceBill;
    private String serviceChargeBill;
}