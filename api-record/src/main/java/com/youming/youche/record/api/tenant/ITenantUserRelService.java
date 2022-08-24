package com.youming.youche.record.api.tenant;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.record.domain.tenant.TenantUserRel;
import com.youming.youche.record.dto.tenant.QueryAllTenantDto;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 租户会员关系 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
public interface ITenantUserRelService extends IService<TenantUserRel> {

    /**
     * 查询租户会员关系
     *
     * @param userId   用户编号
     * @param tenantId 车队id
     */
    List<TenantUserRel> getTenantUserRelListByUserId(Long userId,Long tenantId);

    /**
     * 查询租户会员关系
     *
     * @param userId         用户编号
     * @param sourceTenantId 车队id
     */
    List<TenantUserRel> getTenantUserRel(Long userId,long sourceTenantId);

    /**
     * 查询租户会员信息
     *
     * @param relId 主键id
     * @return
     */
    TenantUserRel getTenantUserRelByRelId(Long relId);

    /**
     * 如果审核表有数据，删除主表，把审核表数据复制到主表
     */
    public void updateUserSalary(TenantUserRel tenantUserRel);


    /**
     * 更新时，把审核表数据置为不可以审，根据输入参数创建审核表记录
     */
    public void updateUserLineVer(Long userId, Long tenantId, Map<String, Object> inParam,String accessToken);

    /**
     * 如果审核表有数据，删除主表，把审核表数据复制到主表
     */
    public void updateUserLine(TenantUserRel tenantUserRel);

    TenantUserRel getTenantUserRelByUserIdAndType(Long userId, Integer carUserType);
    /**
     * 获取租户司机关系，包含未认证的关系
     */
    TenantUserRel getAllTenantUserRelByUserId(long userId,long tenantId);

    /**
     * 获取司机类型
     *
     * @param driverUserId 司机用户编号
     * @param tenantId     车队id
     */
    Integer getCarUserType(Long driverUserId, Long tenantId) throws BusinessException;

    /**
     * 查询已认证司机资料信息
     *
     * @param userId   司机用户编号
     * @param tenantId 车队id
     * @param state    状态已认证
     */
    TenantUserRel getTenantUserRelList(Long userId, Long tenantId, Integer state);

    /**
     * 判断是否是司机
     *
     * @param userId   司机用户编号
     * @param tenantId 车队id
     */
    Boolean isDriver(Long userId, Long tenantId);

    /**
     * 接口编号：10069
     * <p>
     * 接口入参：
     * userId
     * tenantId
     * <p>
     * 接口出参：
     * result true,是自有司机；false，非自有司机
     */
    Boolean checkOwnDriver(Long userId, Long tenantId);

    /**
     * 查询租户会员关系
     *
     * @param userId   用户编号
     * @param tenantId 车队id
     */
    TenantUserRel getTenantUserRelByUserId(Long userId, Long tenantId);

    /**
     * 查询已认证司机资料信息
     *
     * @param driverUserId 司机用户编号
     * @param carUserType  车辆类型
     */
    List<TenantUserRel> getTenantUserRelsAllTenant(Long driverUserId, Integer carUserType);

    /**
     * 获取所有与司机关联的车队  40047
     */
    List<QueryAllTenantDto> queryAllTenant(String accessToken);

}
