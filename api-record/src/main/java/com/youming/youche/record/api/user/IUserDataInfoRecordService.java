package com.youming.youche.record.api.user;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.record.domain.user.UserDataInfo;
import com.youming.youche.record.dto.GetTenantsDto;
import com.youming.youche.record.dto.StaffDataInfoDto;
import com.youming.youche.record.dto.user.AuthInfoDto;
import com.youming.youche.record.vo.StaffDataInfoVo;
import com.youming.youche.record.vo.UpdateUserDataInfoVo;
import com.youming.youche.record.vo.UserDataInfoBackVo;
import com.youming.youche.record.vo.user.CarAuthInfoVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户资料信息 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-17
 */
public interface IUserDataInfoRecordService extends IBaseService<UserDataInfo> {

    /**
     * 主键查询用户信息
     */
    UserDataInfo getUserDataInfo(Long driverUserId);

    /**
     * 通过手机号获取用户信息
     *
     * @param loginAcct 用户账号
     */
    UserDataInfo getDriver(String loginAcct);

    /**
     * 身份证号验证
     *
     * @param identification 身份证号码
     * @param userDataInfo
     * @return
     * @throws Exception
     */
    UserDataInfo isExistIdentification(String identification, UserDataInfo userDataInfo);

    /**
     * 查询用户登录信息
     */
    UserDataInfo getUserDataInfoByAccessToken(String accessToken);

    /**
     * 查询用户登录信息
     */
    LoginInfo getLoginInfoByAccessToken(String accessToken);

    /**
     * 查询用户登录信息
     */
    SysUser getSysUserByAccessToken(String accessToken);

    /**
     * 查询用户信息
     *
     * @param mobilePhone 联系人手机号
     * @param tenantFlag
     */
    UserDataInfo getUserDataInfoByMoblile(String mobilePhone, boolean tenantFlag, String accessToken);

    /**
     * 姓名、手机号码同步
     *
     * @param userId      用户编号
     * @param mobilePhone 联系人手机号
     * @param linkman     联系人姓名
     */
    void overrideMobilePhoneAndLinkman(Long userId, String mobilePhone, String linkman);

    /**
     * 实现功能: 分页查询本车队所有司机以及所有平台司机
     *
     * @param linkman   司机姓名
     * @param loginAcct 司机账号
     * @param current   分页参数
     * @param size      分页参数
     * @return 本车队所有司机及所有平台司机
     */
    Page<UserDataInfoBackVo> queryAllTenantDriverOrPtDriver(Page<UserDataInfoBackVo> page, String linkman,
                                                            String loginAcct, String accessToken);

    /**
     * 实现功能: 根据手机号查询用户名称
     *
     * @param mobile 接收人手机号
     * @param flag   1-招商车，2-挂靠车
     * @return
     */
    Map<String, String> queryUserInfoByMobile(String mobilePhone, Integer flag, String accessToken);

    /**
     * 根据userId获取员工姓名
     */
    public List<String> querStaffName(List<Long> userIdList);

    /**
     * 获取用户用户名
     *
     * @param userId 用户编号
     */
    String getUserName(long userId);

    /**
     * 10068 修改员工与收款人信息
     *
     * @param userId       - 用户编号
     * @param userName     - 用户名称
     * @param receiverName - 收款人名称
     * @param idCard       - 身份证号
     */
    void updateUserDataInfo(UpdateUserDataInfoVo updateUserDataInfoVo);

    /**
     * 接口编号 10031
     * <p>
     * 接口入参：
     * <p>
     * 接口出参：
     * isMultiTenantUser    是否有多个租户  true 进入 false 不进入
     * tenantList           isMultiTenantUser为true的情况下，返回所有的车队信息 key为车队id  value为车队名称
     * 小程序获取租户列表
     */
    GetTenantsDto getTenants(String accessToken);

    void selTenant(Long tenantId, Integer appCode, Long receiverId, String openId, String accessToken);

    /**
     * 接口编号 10022
     * <p>
     * 接口入参：
     * billId	              手机号码
     * appCode               小程序区分：1 服务商  2 驻场
     * <p>
     * 接口出参：
     * info                  0正常 1 手机号码错误  2 不是有效员工  待扩展
     * 检查手机号码
     */
    Integer checkMobilePhoneForWx(String billId, Integer appCode);

    /**
     * 接口名称：修改支付密码
     * 接口编号：10011
     * 接口入参：
     * userId 用户编号
     * oldPwd 旧密码
     * newPwd 新密码
     * 接口出参：
     * result 操作结果：Y-成功，N-失败
     */
    void modifyPayPwd(Long userId, String oldPwd, String newPwd, Integer operType, String accessToken);

    /***
     * 接口名称：找回支付密码接口
     * 接口编码:10015
     * 接口入参：
     *       billId	 用户手机
     *       userId	 用户编号
     *	    captcha	 验证码
     *		loginPasswd 登录密码
     *       newPayPasswd 新支付密码 （需加密）
     * 接口出参：
     *       result 操作结果：Y-成功，N-失败
     *
     */
    void payPasswdReset(Long userId, String newPayPasswd, String captcha,
                        String billId, String loginPasswd, int channel, String accessToken);

    /**
     * 接口名称：修改手机号
     * 接口编号：10010
     * <p>
     * 接口入参：
     * userId      用户编号
     * billId 新手机号码
     * identiCode 	验证码      base 加密
     * loginPwd	       旧登录密码  base 加密
     * <p>
     * 接口出参：
     * result 操作结果：Y-成功，N-失败
     * 修改登录手机号码
     */
    void modifyLoginPhone(String billId, Long userId, String identiCode, String loginPwd, String accessToken);

    /***
     * 接口名称：设置/修改 登录密码接口
     * 接口编码:10008
     * 接口入参：
     *        billId	                  用户手机
     *        oldPassword  旧密码 （修改密码必传）Base64加密
     *        newPassword  新密码 （需加密）
     *        operType     1-设置登录密码，2-修改登录密码
     * 接口出参：
     *       result 操作结果：Y-成功，N-失败
     */
    void loginPasswdMng(Integer operType, String billId, String newPassword, String oldPassword, String accessToken);

    /***
     * 接口名称：忘记登录密码接口
     * 接口编码:10009
     * 接口入参：
     *       billId	 用户手机
     *	    captcha	 验证码
     *       password 新密码 （需加密）
     * 接口出参：
     *       result 操作结果：Y-成功，N-失败
     *
     */
    void loginPasswdReset(String billId, String captcha, String newPassword);

    UserDataInfo selectUserType(Long userInfoId);

    /**
     * 14005
     * 当前车队 员工分页列表查询<br/>
     * <p>
     * 入参：
     * <ul>
     *     <li>loginAcct        账号（手机号）     </li>
     * 	   <li>linkman          姓名              </li>
     * 	   <li>employeeNumber   员工工号          </li>
     * 	   <li>staffPosition    职位              </li>
     * 	   <li>lockFlag         状态  1启用，2禁用 </li>
     * 	   <li>orgId            员工关联的部门</li>
     * </ul>
     */
    Page<StaffDataInfoDto> queryStaffInfo(StaffDataInfoVo vo, Integer pageNum, Integer pageSize, String accessToken);

    /**
     * 接口编号 10061
     * <p>
     * 入参：
     * <ul>
     *     <li>loginAcct        账号（手机号）     </li>
     * 	   <li>linkman          姓名              </li>
     * 	   <li>employeeNumber   员工工号          </li>
     * 	   <li>staffPosition    职位              </li>
     * 	   <li>lockFlag         状态  1启用，2禁用 </li>
     * 	   <li>orgId            员工关联的部门</li>
     * 出参：
     *        linkman        姓名
     *        identification 身份证号码
     *        loginAcct      手机号
     *        employeeNumber 员工工号
     *        staffPosition  职位
     *        tenantName     归属车队名称
     *        adminUserMobile 超管手机号
     * 小程序接口，根据跟单人员，模糊匹配跟单人员手机号
     */
    List<StaffDataInfoDto> getUserInfoByUserName(StaffDataInfoVo vo, String accessToken);

    /**
     * 我的认证资料查询接口 10013
     *
     * @param userId 用户编号
     */
    AuthInfoDto queryAuthInfo(Long userId);

    /**
     * 10014 我的认证资料保存接口
     */
    void saveCarAuthInfo(CarAuthInfoVo vo, String accessToken);

}
