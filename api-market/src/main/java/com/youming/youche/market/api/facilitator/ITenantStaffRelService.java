package com.youming.youche.market.api.facilitator;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.market.domain.facilitator.TenantStaffRel;

import java.util.List;

/**
 * <p>
 * 员工信息 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-07
 */
public interface ITenantStaffRelService extends IBaseService<TenantStaffRel> {

    /**
     * 查询员工信息
     *
     * @param userId     用户编号
     * @param user       当前用户信息
     * @param tenantFlag
     * @return
     */
    List<TenantStaffRel> getTenantStaffRel(Long userId, LoginInfo user, Boolean tenantFlag);

    /**
     * 查询用户信息
     *
     * @param userId 用户编号
     * @return
     */
    List<TenantStaffRel> getTenantStaffRelByUserId(Long userId);
}
