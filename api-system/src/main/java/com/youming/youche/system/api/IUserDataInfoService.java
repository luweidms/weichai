package com.youming.youche.system.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.dto.*;
import com.youming.youche.system.dto.user.LocalUserInfoDto;
import com.youming.youche.system.vo.CreateUserVo;
import com.youming.youche.system.vo.UpdateUserVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户资料信息 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-12-25
 */
public interface IUserDataInfoService extends IBaseService<UserDataInfo> {

	/**
	 * 方法实现说明 获取当前登录者用户信息
	 * @author      terry
	 * @param accessToken
	 * @return      com.youming.youche.system.dto.UserDataInfoAndOrganizeDto
	 * @exception
	 * @date        2022/5/31 16:01
	 */
	UserDataInfoAndOrganizeDto get(String accessToken);

	/**
	 * 方法实现说明 查询用户列表
	 * @author      terry
	 * @param accessToken
	* @param pageNum
	* @param pageSize
	* @param phone 手机号码
	* @param linkman 姓名
	 * @return      com.baomidou.mybatisplus.core.metadata.IPage<com.youming.youche.system.domain.UserDataInfo>
	 * @exception
	 * @date        2022/5/31 16:02
	 */
	IPage<UserDataInfo> get(String accessToken, Integer pageNum, Integer pageSize, String phone, String linkman);

	/**
	 * 方法实现说明 查询用户信息+职位等员工信息  列表
	 * @author      terry
	 * @param accessToken
	* @param orgId 部门id
	* @param pageNum
	* @param pageSize
	 * @return      com.baomidou.mybatisplus.core.metadata.IPage<com.youming.youche.system.dto.OrganizeStaffDto>
	 * @exception
	 * @date        2022/5/31 16:04
	 */
	IPage<OrganizeStaffDto> get(String accessToken, Long orgId, Integer pageNum, Integer pageSize);

	/**
	 * 方法实现说明 新建用户信息并返回用户信息id
	 * @author      terry
	 * @param createUserVo {@link CreateUserVo}
	* @param accessToken
	 * @return      java.lang.Long
	 * @exception
	 * @date        2022/5/31 16:05
	 */
	Long create(CreateUserVo createUserVo, String accessToken);

	/**
	 * 方法实现说明 根据用户信息id修改信息  返回员工id
	 * @author      terry
	 * @param updateUserVo {@link CreateUserVo}
	* @param accessToken
	 * @return      java.lang.Long
	 * @exception
	 * @date        2022/5/31 16:05
	 */
    Long update(UpdateUserVo updateUserVo, String accessToken);

	/**
	 * 方法实现说明 根据用户信息id删除用户信息，  返回登录账户id
	 * @author      terry
	 * @param id 用户信息id
	* @param accessToken
	 * @return      java.lang.Long
	 * @exception
	 * @date        2022/5/31 16:05
	 */
    Long remove(Long id, String accessToken);

	/**
	 * 方法实现说明  根据用户id列表查询用户信息列表
	 * @author      terry
	 * @param userIdList 用户id集合
	 * @return      java.util.List<com.youming.youche.system.domain.UserDataInfo>
	 * @exception
	 * @date        2022/5/31 16:08
	 */
	List<UserDataInfo> getUserDataInfoList(List<Long> userIdList);

	/**
	 * 根据userId获取员工姓名
	 */
	public List<String> querStaffName(List<Long> userIdList);

	/**
	 * 方法实现说明 获取登录者用户信息
	 * @author      terry
	 * @param accessToken 令牌
	 * @return      com.youming.youche.commons.domain.LoginInfo
	 * @exception
	 * @date        2022/5/31 16:09
	 */
	LoginInfo getLoginInfoByAccessToken(String accessToken);

	/**
	 * 方法实现说明 查询手机号码的用户信息
	 * @author      terry
	 * @param phone 手机号码
	 * @return      com.youming.youche.commons.domain.LoginInfo
	 * @exception
	 * @date        2022/5/31 16:09
	 */
    UserDataInfo getPhone(String phone);

	/**
	 * 根据userId获取员工姓名.
	 */
	List<String> querStaffNameByUserIds(List<Long> userIdList);

	/**
	 * 方法实现说明 通过用户信息id列表，查询员工姓名
	 * @author      terry
	 * @param userIdList
	 * @return      java.util.Map<java.lang.Long,java.lang.String>
	 * @exception
	 * @date        2022/5/31 16:10
	 */
	Map<Long, String> querStaffNameAndIdByUserIds(List<Long> userIdList);

	/**
	 * 获取用户记录
	 *
	 * @param driverUserId 用户id
	 */
	UserDataInfo getUserDataInfo(Long driverUserId);

	/**
	 * @param userId 用户id
	 * @return 联系人姓名
	 */
	String getUserName(long userId);

	/**
	 * 检查司机信息完整性是否符号要求
	 * @param userId
	 * @return true:符合，false：不符合
	 */
	boolean checkCompleteness(long userId, int goodsType);


	/**
	 * 查询代收车队
	 * @return
	 * @throws Exception
	 */
	Page<DriverAndReceiverInfoOutDto> getVirtualTenantList(Integer pageNum,Integer pageSize,
														   String phone, String name,
														   Boolean includeDriver);

	/**
	 * 接口编号 10036
	 * <p>
	 * 接口入参：
	 * userId         用户编号
	 * 接口出参：
	 * linkman        姓名
	 * identification 身份证号码
	 * loginAcct      账号
	 * employeeNumber 员工工号
	 * staffPosition  职位
	 * tenantName     归属车队名称
	 * adminUserMobile 超管手机号
	 * 企业驻场——首页——个人信息
	 */
	LocalUserInfoDto getLocalUserInfo(Long userId, String accessToken);

    /**
     * 查找用户信息是否存在
     *
     * @param userId 用户id
     * @return
     */
    Boolean doInit(long userId);

	/**
	 * 查找用户信息是否存在
	 *
	 * @param userId 用户id
	 * @return
	 */
	Boolean doInits(Long userId);
	/**
	 * 方法实现说明 通过用户信息id查询用户信息
	 * @author      terry
	 * @param userInfoId 用户信息id
	 * @return      com.youming.youche.system.domain.UserDataInfo
	 * @exception
	 * @date        2022/5/31 16:11
	 */
	UserDataInfo selectUserType(Long userInfoId);

	/**
	 * 方法实现说明 查询司机用户等信息
	 * @author      terry
	 * @param linkman
	* @param mobilePhone
	* @param token
	* @param pageSize
	* @param pageNum
	 * @return      com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.youming.youche.system.dto.UserDataLinkManDto>
	 * @exception
	 * @date        2022/5/31 16:12
	 */
	Page<UserDataLinkManDto>  doQueryBackUserList(String linkman, String mobilePhone,
												  String token, Integer pageSize, Integer pageNum);

	/**
	 * 判断短信验证码与登录密码是否正确 10023
	 *
	 * @param userId      用户编号
	 * @param billId      手机号
	 * @param captcha     验证码 （需加密（base64））
	 * @param loginPasswd 登录密码 （需加密（base64））
	 * @param channel     渠道 1-web，2-app，3-小程序
	 */
	void checkCaptchaAndLoginPasswd(Long userId, String billId, String captcha, String loginPasswd, Integer channel);

	/**
	 * 接口编码10070
	 * 修改司机路哥运输协议
	 */
	void updateLugeAgreement(Long userId, Long lugeAgreement, String lugeAgreementUrl);

	/**
	 * 方法实现说明 根据用户信息id查询司机信息
	 * @author      terry
	 * @param userId
	* @param tenantId
	 * @return      com.youming.youche.system.domain.UserDataInfo
	 * @exception
	 * @date        2022/5/31 16:14
	 */
	UserDataInfo getOwnDriver(Long userId, Long tenantId);

	/**
	 * 40037
	 * 加油二维码(找油网)
	 */
	OrCodeByZhaoYouDto orCodeByZhaoYou(String plateNumber, Long userId, String userName, String userPhone);


	UserDataInfo saveReturnId(UserDataInfo userDataInfo);

}
