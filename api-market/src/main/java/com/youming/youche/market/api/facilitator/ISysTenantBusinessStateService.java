package com.youming.youche.market.api.facilitator;

import com.youming.youche.market.domain.facilitator.SysTenantBusinessState;
import com.youming.youche.commons.base.IBaseService;

/**
 * <p>
 * 车队经营状况 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-14
 */
public interface ISysTenantBusinessStateService extends IBaseService<SysTenantBusinessState> {

    /**
     * 查询车队经营状况
     *
     * @param tenantId 车队id
     * @return
     */
    SysTenantBusinessState queryByTenantId(Long tenantId);

}
