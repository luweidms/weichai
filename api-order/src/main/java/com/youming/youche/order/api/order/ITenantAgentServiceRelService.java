package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.TenantAgentServiceRel;
import com.youming.youche.order.dto.SumQuotaAmtDto;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
public interface ITenantAgentServiceRelService extends IBaseService<TenantAgentServiceRel> {

    /**
     * 授信金额
     * @param tenantId
     * @param agentServiceType
     * @return
     */
    SumQuotaAmtDto getSumQuotaAmtListByTenantId(Long tenantId, Integer agentServiceType);
}
