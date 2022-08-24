package com.youming.youche.record.api.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.commons.domain.SysUser;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-20
 */
public interface ISysUserService extends IService<SysUser> {

    /**
     * 获取用户信息
     *
     * @param userId 用户编号
     */
    SysUser getSysUserByUserId(Long userId);

    /**
     * 如果新增员工时，员工不存于系统，则使用此方法往操作员表添加记录
     */
     SysUser insertSysUser(String loginAcct, Long userId, String operatorName, Integer lockFlag,
                                         String pwd, Long tenantId, Integer verifyStatus, String tenantCode,String accessToken);

    /**
     * 获取用户信息
     *
     * @param userId 用户id
     * @param billId 手机号
     * @return
     */
    SysUser getSysUserByUserIdOrPhone(Long userId, String billId);

    /**
     * 根据用户编号查询用户信息
     *
     * @param userInfoId 用户编号
     * @return
     */
    SysUser getByUserInfoId(Long userInfoId);

    /**
     * 获取用户信息
     *
     * @param userId 用户id
     * @param billId 手机号
     * @return
     */
    SysUser getSysOperatorByUserIdOrPhone(Long userId, String billId);

    /**
     * 获取用户信息
     *
     * @param userId 用户编号
     */
    SysUser getSysOperatorByUserId(Long userId);

    /**
     * 根据登录账号和租户查询操作员信息
     */
    SysUser getSysOperatorByLoginAcct(String loginAcct, long tenantId);

}
