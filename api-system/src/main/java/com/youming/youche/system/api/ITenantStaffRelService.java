package com.youming.youche.system.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.system.domain.TenantStaffRel;
import com.youming.youche.system.dto.TenantStaffDto;
import com.youming.youche.system.dto.UserDataInfoAndStaffs;

import java.util.List;

/**
 * <p>
 * 员工信息 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-01-04
 */
public interface ITenantStaffRelService extends IBaseService<TenantStaffRel> {

    /**
     * 方法实现说明 获取当前用户车队角色信息
     *
     * @param accessToken
     * @return java.util.List<com.youming.youche.system.domain.TenantStaffRel>
     * @throws
     * @author terry
     * @date 2022/1/4 15:31
     */
    List<TenantStaffRel> getTenantStaff(String accessToken);

    /**
     * 方法实现说明 查询车队员工信息
     *
     * @param userInfoId * @param tenantId
     * @return com.youming.youche.system.domain.TenantStaffRel
     * @throws
     * @author terry
     * @date 2022/1/6 14:05
     */
    TenantStaffRel getTenantStaffByUserInfoIdAndTenantId(Long userInfoId, Long tenantId);

    /**
     * 方法实现说明 逻辑删除员工与车队关系
     * @author      terry
     * @param userInfoId
    * @param tenantId
     * @return      boolean
     * @exception
     * @date        2022/5/31 15:51
     */
    boolean delByUserInfoIdAndTenantId(Long userInfoId, Long tenantId);

    /**
     * 方法实现说明 查询用户对应的所有员工姓名
     *
     * @param userInfoId * @param tenantId
     * @return com.youming.youche.system.domain.TenantStaffRel
     * @throws
     * @author terry
     * @date 2022/1/6 14:05
     */
    List<TenantStaffRel> getTenantStaffByUserInfoId(Long userInfoId);

    /**
     * 方法实现说明 查询车队员工信息
     *
     * @param userInfoId * @param tenantId * @param state
     * @return com.youming.youche.system.domain.TenantStaffRel
     * @throws
     * @author terry
     * @date 2022/1/6 14:05
     */
    TenantStaffRel getTenantStaffByUserInfoIdAndTenantIdAndState(Long userInfoId, Long tenantId, int state);


    /**
     * 获取车队所有员工信息
     *
     * @param accessToken
     * @param pageNum
     * @param pageSize
     * @param phone
     * @param linkman
     * @param number
     * @param position
     * @param orgId
     * @param lockFlag
     * @return java.util.List<com.youming.youche.system.dto.TenantStaffDto>
     * @throws
     * @author terry
     * @date 2022/1/12 21:02
     */
    IPage<TenantStaffDto> get(String accessToken, Integer pageNum, Integer pageSize, String phone, String linkman, String number, String position, Integer lockFlag, String orgId);

    /**
     * 获取车队员工信息
     *
     * @param accessToken
     * @param userInfoId
     * @return java.util.List<com.youming.youche.system.dto.TenantStaffDto>
     * @throws
     * @author terry
     * @date 2022/1/12 21:02
     */
    List<TenantStaffDto> get(String accessToken, Long userInfoId);

    /**
     * 实现功能: 查询归属人列表
     *
     * @param lockFlag 是否启用（1：启用，2停用）
     * @param userAccount 员工账号
     * @param staffName 员工姓名
     * @return
     */
    Page<TenantStaffRel> queryStaffInfo(Page<TenantStaffRel> page, Integer lockFlag, String accessToken,Long userAccount,String staffName);

    /**
     * 方法实现说明 通过手机号码查询员工信息
     * @author      terry
     * @param phone 手机号码
     * @return      com.youming.youche.system.dto.UserDataInfoAndStaffs
     * @exception
     * @date        2022/5/31 15:53
     */
    UserDataInfoAndStaffs selectByPhone(String phone);

    /**
     * 实现功能: 查询跟单人列表
     *
     * @param lockFlag 是否启用（1：启用，2停用）
     * @param userAccount 员工账号
     * @param staffName 员工姓名
     * @return
     */
    Page<TenantStaffRel> getStaffInfo(Page<TenantStaffRel> page, Integer lockFlag, String accessToken,Long userAccount,String staffName);


    /**
     * 实现功能: 查询跟单人列表（里面有跟单人手机号）
     *
     * @param lockFlag 是否启用（1：启用，2停用）
     * @param userAccount 员工账号
     * @param staffName 员工姓名
     * @return
     */
    Page<TenantStaffRel> getOrderStaffInfo(Page<TenantStaffRel> page, Integer lockFlag, String accessToken,Long userAccount,String staffName);


    /**
     * 实现功能: 查询本车队跟单人列表（里面有跟单人手机号）
     *
     * @param lockFlag 是否启用（1：启用，2停用）
     * @param userAccount 员工账号
     * @param staffName 员工姓名
     * @return
     */
    Page<TenantStaffRel> getOrderStaffInfoBytenantId(Page<TenantStaffRel> page, Integer lockFlag, String accessToken,Long userAccount,String staffName);

    /**
     * 判断是否是员工
     * @param userId
     * @param tenantId
     * @return
     */
    Boolean isStaff(Long userId, Long tenantId);

}
