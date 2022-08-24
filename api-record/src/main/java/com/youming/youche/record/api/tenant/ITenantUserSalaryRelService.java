package com.youming.youche.record.api.tenant;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.record.domain.tenant.TenantUserSalaryRel;
import com.youming.youche.record.dto.UserSalaryDto;

import java.util.List;

/**
 * <p>
 * 租户自有司机收入信息 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
public interface ITenantUserSalaryRelService extends IService<TenantUserSalaryRel> {
    //租户自有司机收入表  审核表start
    TenantUserSalaryRel getTenantUserRalaryRelByUserId(Long userId) throws Exception;

    /**
     * 司机收入记录
     */
    TenantUserSalaryRel getTenantUserRalaryRelByRelId(Long tenantUserRelId) throws BusinessException;

    /**
     * 自由司机收入信息
     *
     * @param userId   用户编号
     * @param tenantId 车队id
     */
    TenantUserSalaryRel getTenantUserRalaryRelByUserId(Long userId, Long tenantId);

    /**
     * 自由司机收入信息
     *
     * @param tenantId 车队id
     */
    List<UserSalaryDto> doQueryOwnDriverAll(Long tenantId);
}
