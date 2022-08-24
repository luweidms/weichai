package com.youming.youche.system.api;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.system.domain.UserType;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
public interface ISysUserService extends IBaseService<SysUser> {

	/**
	 * 方法实现说明 获取当前登录者登录账户信息
	 * @author      terry
	 * @param
	 * @return      com.youming.youche.commons.response.ResponseResult
	 * @exception
	 * @date        2022/5/31 15:16
	 */
	SysUser get(String accessToken);

	/**
	 * 方法实现说明 获取用户信息对应的登录账户信息
	 * @author      terry
	 * @param userInfoId 用户信息id
	 * @return      com.youming.youche.commons.response.ResponseResult
	 * @exception
	 * @date        2022/5/31 15:16
	 */
	SysUser getByUserInfoId(Long userInfoId);



	/**
	 * 方法实现说明 保存用户信息
	 * 返回主键id
	 * @author      terry
	 * @param sysUser
	 * @return      java.lang.Long
	 * @exception
	 * @date        2022/5/31 15:19
	 */
    Long saveSysUser(SysUser sysUser);

	/**
	 * 方法实现说明 保存登录账户和用户信息
	 * @author      terry
	 * @param loginAcct
	* @param userId
	* @param password
	* @param linkman
	 * @return      long 登录账户id
	 * @exception
	 * @date        2022/5/31 15:28
	 */
	long saveServerChildUser(String loginAcct, Long userId, String s, String linkman);



	/**
	 * 方法实现说明 通过用户信息或者订单信息查询登录者信息
	 * @author      terry
	 * @param userId 用户信息
	 * @param billId 手机号码
	 * @return      com.youming.youche.commons.domain.SysUser
	 * @exception
	 * @date        2022/5/31 15:21
	 */
	SysUser getSysOperatorByUserDatainfoIdOrPhone(Long userId, String billId);
	/**
	 * 判断用户是否已存在
	 * userId和billId只传其一就行，
	 * userId没有时可以填null或者0,
	 * billId可以传""或者null，但是两者必须传其一
	 * SysOperator==null表示不存在或者反之
	 * @param userId
	 * @param billId 手机号码
	 * @return
	 */
	SysUser isExistUserInfo(Long userId, String billId);

	/**
	 * 方法实现说明 获取用户信息对应的登录账户信息
	 * @author      terry
	 * @param userInfoId 用户信息id
	 * @return      com.youming.youche.commons.response.ResponseResult
	 * @exception
	 * @date        2022/5/31 15:16
	 */
	SysUser getSysOperatorByUserId(Long userInfoId);

	/**
	 * 方法实现说明 通过用户信息或者订单信息查询登录者信息
	 * @author      terry
	 * @param userId 用户信息id
	 * @param billId 手机号码
	 * @param tenantId
	 * @return      com.youming.youche.commons.domain.SysUser
	 * @exception
	 * @date        2022/5/31 15:21
	 */
	SysUser getSysOperatorByUserIdOrPhone(Long userId, String billId, Long tenantId);

	/**
	 * 方法实现说明 通过用户信息或者订单信息查询登录者信息
	 * @author      terry
	 * @param userId 用户信息
	 * @param billId 手机号码
	 * @return      com.youming.youche.commons.domain.SysUser
	 * @exception
	 * @date        2022/5/31 15:21
	 */
	SysUser getSysOperatorByUserIdOrPhone(Long userId, String billId);
	/**
	 * 接口名称：修改手机号校验登录密码
	 * 接口编号：10028
	 * <p>
	 * 接口入参：
	 * userId      用户编号
	 * loginPwd	   旧登录密码  base 加密
	 * <p>
	 * 接口出参：
	 * result 操作结果：Y-成功，N-失败
	 */
	void valideLoginPwdForUpdatePhone(Long userId, String loginPwd);

	/**
	 * 方法实现说明 查询车队所有的登录账户信息
	 * @author      terry
	 * @param
	 * @return      java.util.List<com.youming.youche.commons.domain.SysUser>
	 * @exception
	 * @date        2022/5/31 15:20
	 */
	List<SysUser> getSysUserAll();

	/**
	 * 接口编码:11007
	 * 接口入参：
	 * billId	         用户手机
	 * codeType	         验证码类型  1：注册验证码2、重置/忘记登录验证码3、支付密码 11、修改登录手机验证码17、登录验证码（其他待扩展）
	 * <p>
	 * 获取短信验证码接口
	 */
	void getMessageAuthCode(String billId, Integer codeType);

	List<UserType> getUserType(long userId, Long tenantId);

	/***
	 * @Description: 手机号查询userId
	 * @Author: luwei
	 * @Date: 2022/7/13 16:08
	 * @Param phone:
	 * @return: java.util.List<com.youming.youche.commons.domain.SysUser>
	 * @Version: 1.0
	 **/
	List<SysUser> getPhoneUser(String phone);
}
