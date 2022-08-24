package com.youming.youche.record.api.user;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.record.domain.tenant.TenantUserRel;
import com.youming.youche.record.domain.user.UserLineRel;

import java.util.List;

/**
 * <p>
 * 用户心愿线路表 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
public interface IUserLineRelService extends IService<UserLineRel> {
    public List<UserLineRel> getUserLineRelByUserId(Long userId, Long tenantId);

    /**
     * 把主表记录移到审核表作为历史数据，删除主表数据
     */
    public void removeUserLineRel(Long userId, Long tenantId,String accessToken) throws Exception;


    /**
     * 把主表记录移到审核表作为历史数据，删除主表数据
     */
    public void removeTenantUserSalaryRel(TenantUserRel tenantUserRel) throws Exception;
}
