package com.youming.youche.market.dto.facilitator;

import lombok.Data;

import java.io.Serializable;
@Data
public class CooperationTenantDto implements Serializable {
    private Long tenantId;
    private String tenantName;
    private String tenantLinkPhone;
    //车队联系人
    private String tenantLinkMan;
    /**
     * 站点ID
     */
    private Long productId;
    private String createDate;
    private String floatBalanceBill;
    private String fixedBalanceBill;
    private String floatBalance;
    private Long fixedBalance;
    private Integer isBillAbility;
}
