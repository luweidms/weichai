package com.youming.youche.record.api.tenant;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.record.domain.tenant.TenantUserRelVer;

import java.util.List;

/**
 * <p>
 * 租户会员关系 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
public interface ITenantUserRelVerService extends IService<TenantUserRelVer> {

    /**
     * 查询租户资料
     *
     * @param userId 用户编号
     */
    TenantUserRelVer getTenantUserRelVerByUserId(Long userId) throws Exception;

    /**
     * 询租户资料
     *
     * @param userId   用户编号
     * @param tenantId 车队id
     */
    TenantUserRelVer getTenantUserRelVer(Long userId, Long tenantId);

    /**
     * 询租户资料
     *
     * @param userId   用户编号
     * @param tenantId 车队id
     * @param verState 审核数据状态
     * @return
     */
    List<TenantUserRelVer> getTenantUserRelVer(Long userId, Long tenantId, int verState);

    /**
     * 询租户资料
     *
     * @param id 关系ID
     */
    List<TenantUserRelVer> getTenantUserRelVerByRelId(Long id);

}
