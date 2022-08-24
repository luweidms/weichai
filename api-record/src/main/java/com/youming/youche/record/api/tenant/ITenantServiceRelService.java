package com.youming.youche.record.api.tenant;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.record.domain.tenant.TenantServiceRel;

import java.util.List;

/**
 * <p>
 * 服务商与租户关系 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
public interface ITenantServiceRelService extends IService<TenantServiceRel> {

    /**
     * 查询租户与服务商关系
     *
     * @param serviceUserId 服务商id
     * @param tenantId      车队id
     * @return
     * @throws Exception
     */
    List<TenantServiceRel> getTenantServiceRelList(Long serviceUserId, Long tenantId) throws Exception;

    /**
     * 查询租户与服务商关系
     *
     * @param serviceId 服务商id
     * @param tenantId  车队id
     */
    TenantServiceRel getTenantServiceRel(long tenantId, long serviceId);

}
