package com.youming.youche.market.vo.facilitator;

import com.youming.youche.market.domain.facilitator.TenantServiceRel;
import com.youming.youche.market.domain.facilitator.TenantServiceRelVer;
import lombok.Data;

@Data
public class FacilitatorDetailedVo extends TenantServiceRel {
    private TenantServiceRelVer tenantServiceRelVer;
}
