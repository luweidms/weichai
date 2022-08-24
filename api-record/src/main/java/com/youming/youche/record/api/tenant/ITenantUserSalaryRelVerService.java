package com.youming.youche.record.api.tenant;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.record.domain.tenant.TenantUserSalaryRelVer;

import java.util.List;

/**
 * <p>
 * 租户自有司机收入信息 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
public interface ITenantUserSalaryRelVerService extends IService<TenantUserSalaryRelVer> {

    /**
     * 获取可以审核的工资关系记录（只有一条）
     */
    List<TenantUserSalaryRelVer> getTenantUserSalaryRelVers(long relId) throws Exception;

    /**
     * 获取可以审核的工资关系记录（只有一条）
     *
     * @param tenantUserRelId 关系主键
     * @param verState        审核状态
     */
    List<TenantUserSalaryRelVer> getTenantUserSalaryRelVer(Long tenantUserRelId, int verState);

    /**
     * 查询待审数据
     */
    TenantUserSalaryRelVer getTenantUserSalaryRelVer(long tenantUserRelId);
}
