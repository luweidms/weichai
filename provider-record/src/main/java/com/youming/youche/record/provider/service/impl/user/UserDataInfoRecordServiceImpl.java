package com.youming.youche.record.provider.service.impl.user;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.encrypt.K;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.record.api.account.IAccountBankRelService;
import com.youming.youche.record.api.dirverInfo.IDriverInfoExtService;
import com.youming.youche.record.api.sys.ISysUserService;
import com.youming.youche.record.api.tenant.ITenantStaffRelService;
import com.youming.youche.record.api.tenant.ITenantUserRelService;
import com.youming.youche.record.api.tenant.ITenantUserRelVerService;
import com.youming.youche.record.api.tenant.IUserReceiverInfoService;
import com.youming.youche.record.api.user.IUserDataInfoRecordService;
import com.youming.youche.record.api.user.IUserDataInfoVerService;
import com.youming.youche.record.api.user.IUserService;
import com.youming.youche.record.common.AuditConsts;
import com.youming.youche.record.common.EncryPwd;
import com.youming.youche.record.common.EnumConsts;
import com.youming.youche.record.common.SysStaticDataEnum;
import com.youming.youche.record.domain.tenant.TenantStaffRel;
import com.youming.youche.record.domain.tenant.TenantUserRel;
import com.youming.youche.record.domain.tenant.TenantUserRelVer;
import com.youming.youche.record.domain.user.UserDataInfo;
import com.youming.youche.record.domain.user.UserDataInfoVer;
import com.youming.youche.record.domain.user.UserReceiverInfo;
import com.youming.youche.record.dto.GetTenantsDto;
import com.youming.youche.record.dto.StaffDataInfoDto;
import com.youming.youche.record.dto.user.AuthInfoDto;
import com.youming.youche.record.provider.mapper.user.UserDataInfoRecordMapper;
import com.youming.youche.record.provider.utils.ReadisUtil;
import com.youming.youche.record.provider.utils.SysCfgUtil;
import com.youming.youche.record.vo.StaffDataInfoVo;
import com.youming.youche.record.vo.UpdateUserDataInfoVo;
import com.youming.youche.record.vo.UserDataInfoBackVo;
import com.youming.youche.record.vo.user.CarAuthInfoVo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.csource.fastdfs.ClientGlobal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * <p>
 * 用户资料信息 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-17
 */
@DubboService(version = "1.0.0")
public class UserDataInfoRecordServiceImpl extends BaseServiceImpl<UserDataInfoRecordMapper, UserDataInfo>
		implements IUserDataInfoRecordService {


	@Autowired
	private UserDataInfoRecordMapper userDataInfoRecordMapper;


	@Resource
	private RedisUtil redisUtil;

	@Resource
	private ReadisUtil readisUtil;

	@Resource
	private LoginUtils loginUtils;


	@Resource
	private ISysUserService iSysUserService;


	@Resource
	private ITenantStaffRelService iTenantStaffRelService;

	@DubboReference(version = "1.0.0")
	ISysTenantDefService sysTenantDefService;

	@Resource
	IUserService iUserService;

	@Resource
	IDriverInfoExtService iDriverInfoExtService;

	@Resource
	IUserReceiverInfoService iUserReceiverInfoService;

	@DubboReference(version = "1.0.0")
    IServiceInfoService iServiceInfoService;

	@DubboReference(version = "1.0.0")
	ISysOperLogService sysOperLogService;

	@Resource
	IUserDataInfoVerService iUserDataInfoVerService;

	@Resource
	IAccountBankRelService iAccountBankRelService;

	@Resource
	ITenantUserRelService iTenantUserRelService;

	@Resource
	ITenantUserRelVerService iTenantUserRelVerService;

	@DubboReference(version = "1.0.0")
	IAuditService iAuditService;

	@Resource
	public BCryptPasswordEncoder passwordEncoder;

	@Override
	public UserDataInfo getUserDataInfo(Long driverUserId) {
		return userDataInfoRecordMapper.selectById(driverUserId);
	}


	@Override
	public Map<String, String> queryUserInfoByMobile(String mobilePhone,Integer flag,String accessToken){

		Map<String, String> returnMap = new HashMap<String, String>();
		returnMap.put("linkman", "");
		returnMap.put("userId", "");

		if (org.apache.commons.lang.StringUtils.isBlank(mobilePhone)) {
			throw new BusinessException("参数错误，手机号为空");
		}
		if (flag < 0) {
			throw new BusinessException("flag参数错误");
		}

		UserDataInfo userDataInfo = baseMapper.queryUserInfoByMobile(mobilePhone);

		//判断是否为超管，是超管则取车队的名称做为账单接收人
		if (userDataInfo != null){
			returnMap.put("linkman", userDataInfo.getLinkman());
			returnMap.put("userId", userDataInfo.getId() + "");

			SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userDataInfo.getId());
			LoginInfo loginInfo = getLoginInfoByAccessToken(accessToken);
			if (sysTenantDef != null){
				if (loginInfo.getTenantId().equals(sysTenantDef.getId()) ){
					throw new BusinessException("账单接收人不能是本车队的超管");
				}

				returnMap.put("linkman", sysTenantDef.getName());
				returnMap.put("attachIsAdminUser", "1");
				returnMap.put("attachTenantId", sysTenantDef.getId() + "");
				return returnMap;
			}
		}
		return returnMap;
	}

	@Override
	public List<String> querStaffName(List<Long> userIdList) {
		QueryWrapper<UserDataInfo> queryWrapper = new QueryWrapper<>();
		queryWrapper.in("TENANT_ID",userIdList);
		List<UserDataInfo> list=baseMapper.selectList(queryWrapper);
		List<String> staffNameList = new ArrayList<>();
		if (com.alibaba.nacos.common.utils.CollectionUtils.isEmpty(list)) {
			return staffNameList;
		}
		for (UserDataInfo userDataInfo : list) {
			staffNameList.add(userDataInfo.getLinkman());
		}
		return staffNameList;
	}

	/**
	 * 通过手机号获取用户信息
	 * @param loginAcct
	 * @return
	 */
	@Override
	public UserDataInfo getDriver(String loginAcct) {
		QueryWrapper<UserDataInfo> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("MOBILE_PHONE",loginAcct);
		List<UserDataInfo> list= userDataInfoRecordMapper.selectList(queryWrapper);
		if(list!=null && list.size()>0){
			return list.get(0);
		}
		return null;
	}

	@Override
	public UserDataInfo isExistIdentification(String identification, UserDataInfo userDataInfo) {
		if (StringUtils.isBlank(identification)) {
			return null;
		}
		if (null != userDataInfo && identification.equals(userDataInfo.getIdentification())) {
			return null;
		}
		List<UserDataInfo> userDataInfoList = this.getUserDataInfoByIdentification(identification);
		if (CollectionUtils.isEmpty(userDataInfoList)) {
			return null;
		}
		return userDataInfoList.get(0);
	}

	public List<UserDataInfo> getUserDataInfoByIdentification(String identification) {
		if (StringUtils.isBlank(identification)) {
			return null;
		}
		QueryWrapper<UserDataInfo> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("IDENTIFICATION",identification);
		return userDataInfoRecordMapper.selectList(queryWrapper);
	}

	 /**
	  * 实现功能: 通过accessToken获取登录用户信息
	  * @param
	  * @return
	  *
	  */
	 @Override
	public UserDataInfo getUserDataInfoByAccessToken(String accessToken) {
		UserDataInfo userDataInfo = null;
		userDataInfo = (UserDataInfo) redisUtil.get("user_info:" + accessToken);
		return userDataInfo;
	}

	@Override
	public LoginInfo getLoginInfoByAccessToken(String accessToken) {

		return loginUtils.get(accessToken);
	}


	@Override
	public SysUser getSysUserByAccessToken(String accessToken) {
		SysUser sysUser = null;
		if (redisUtil.hasKey("user:" + accessToken)) {
			sysUser = (SysUser) redisUtil.get("user:" + accessToken);
		}
		return sysUser;
	}


	@Override
	public String getUserName(long userId){
		String userName="";
		if (userId > 0) {
			UserDataInfo userDataInfo = this.getUserDataInfo(userId);
			if (userDataInfo != null) {
				userName = userDataInfo.getLinkman();
				if (userName == null || userName.equals("")) {
					userName = userDataInfo.getMobilePhone();
				}
			}
		} else {
			throw new BusinessException("用户名不能为空");
		}
		return userName;
	}

	@Override
	public void updateUserDataInfo(UpdateUserDataInfoVo updateUserDataInfoVo) {
		long userId = updateUserDataInfoVo.getUserId();
		String userName = updateUserDataInfoVo.getUserName();
		String receiverName = updateUserDataInfoVo.getReceiverName();
		String idCard = updateUserDataInfoVo.getIdCard();

		if (userId < 0) {
			throw new BusinessException("参数错误，userId不能为空！");
		}

		iUserService.checkExistIdentification(idCard, userId);

		UserDataInfo userInfo = this.getUserDataInfo(userId);

		if (!userInfo.getLinkman().equals(userName) ||
				(null == userInfo.getIdentification() && !idCard.equals("")) ||
				(null != userInfo.getIdentification() && !userInfo.getIdentification().equals(idCard))) {
			iDriverInfoExtService.resetLuGeAuthState(userInfo.getId());
		}

		userInfo.setLinkman(userName);
		userInfo.setIdentification(idCard);
		this.update(userInfo);

		this.modifyUserName(userId, userName);

		UserReceiverInfo userReceiverInfo = iUserReceiverInfoService.getUserReceiverInfoByUserId(userId);

		if (userReceiverInfo != null) {
			userReceiverInfo.setReceiverName(receiverName);
			iUserReceiverInfoService.updateUserReceiverInfo(userReceiverInfo);
		}
	}

	@Override
	public GetTenantsDto getTenants(String accessToken) {
		LoginInfo loginInfo = loginUtils.get(accessToken);

		List<TenantStaffRel> tenantStaffRelList = iTenantStaffRelService.getTenantStaffRelByUserId(loginInfo.getUserInfoId());
		UserReceiverInfo receiverInfo = iUserReceiverInfoService.getUserReceiverInfoByUserId(loginInfo.getUserInfoId());

		if (tenantStaffRelList == null || tenantStaffRelList.isEmpty()) {
			throw new BusinessException("用户未注册或租户已被停用！请联系客服！");
		}

		List<TenantStaffRel> tenantStaffRelNewList = new ArrayList<>();
		for (TenantStaffRel rel : tenantStaffRelList) {
			if (rel.getTenantId() != SysStaticDataEnum.PT_TENANT_ID) {
				tenantStaffRelNewList.add(rel);
			}
		}
		if (null == receiverInfo && tenantStaffRelNewList.isEmpty()) {
			throw new BusinessException("用户未注册或租户已被停用！请联系客服！");
		}


		List<Map> tenantList = getTenantListMap(tenantStaffRelNewList);

		GetTenantsDto dto = new GetTenantsDto();


		if (null != receiverInfo) {
			Map receiverMap = new HashMap();
			receiverMap.put("receiverId", receiverInfo.getId());
			receiverMap.put("receiverName", receiverInfo.getReceiverName());
			tenantList.add(receiverMap);
		}

		dto.setTenantList(tenantList);

		return dto;
	}

	@Override
	public void selTenant(Long tenantId, Integer appCode, Long receiverId, String openId, String accessToken) {
//		LoginInfo loginInfo = loginUtils.get(accessToken);
//
//		//代收人登陆
//		if (receiverId > 0) {
//			UserReceiverInfo userReceiverInfo = iUserReceiverInfoService.getUserReceiverInfoById(receiverId);
//			if (null == userReceiverInfo) {
//				throw new BusinessException("不存在的代收人！");
//			}
//
//			//如果是某个车队的超管（包括小车队），需要返回车队ID
//			SysTenantDef sysTenantDef = iSysTenantDefService.getSysTenantDefByAdminUserId(userReceiverInfo.getUserId());
//			if (null != sysTenantDef) {
//				map.put("tenantId", sysTenantDef.getId());
//			}
//
//			//设置当前登陆人信息
//			attrs.put("roleIds", new ArrayList<Integer>());
//			attrs.put("tenantCode","");
//			currentOperator.setTenantId(-1L);
//			currentOperator.setOrgId(-1L);
//			currentOperator.setUserType(userTF.getUserType(loginInfo.getId(), tenantId, SysStaticDataEnum.LOGIN_CHANNEL.WX_RECEIVER_7));
//
//			map.put("userType", currentOperator.getUserType());
//			map.put("info","Y");
//			return map;
//		}
//
//		SysUserOrgRel sysUserOrgRel = iSysUserOrgRelService.getStasffDefaultOragnizeByUserId(currentOperator.getUserId(), tenantId);
//
//		long orgId = -1L;
//		if (null != sysUserOrgRel) {
//			orgId = sysUserOrgRel.getOrgId();
//		}
//
//		//登录
//		Param param = new Param("openId", openId);
//		param.addParam("appCode", appCode);
//		param.addParam("state", 1);
//
//		List<WechatUserRel> list = weChat.getObjects(WechatUserRel.class, param, false);
//		if(list==null||list.isEmpty()){
//			throw new BusinessException("用户未绑定！");
//		}
//		WechatUserRel wechatUserRel = list.get(0);
//
//		List<Integer> rtnList = PermissionCacheUtil.getRoleIdsByOperatorId(tenantId, currentOperator.getOperId());
//		attrs.put("roleIds", rtnList);
//		attrs.put("tenantCode", tenantSV.getSysTenantDef(tenantId).getTenantCode());
//		map.put("tenantId", tenantId);
//		currentOperator.setTenantId(tenantId);
//		currentOperator.setOrgId(orgId);
//		currentOperator.setUserType(userTF.getUserType(currentOperator.getUserId(), tenantId, SysStaticDataEnum.LOGIN_CHANNEL.WX_TENANT_4));
//		List<Long> entityIdList = PermissionCacheUtil.getEntityIdByRoles(tenantId, rtnList);
//		map.put("entityIds", entityIdList);
//
//		Long tenantAdminUser = tenantSV.getTenantAdminUser(tenantId);
//		map.put("adminUserId", tenantAdminUser);
//
//		if (tenantAdminUser != null) {
//
//			if (tenantAdminUser.longValue() == currentOperator.getUserId().longValue()) {
//				map.put("isAdminUser", true);
//			} else {
//				map.put("isAdminUser", false);
//			}
//			UserDataInfo dataInfo = userSV.getUserDataInfo(tenantAdminUser);
//			map.put("adminUserMobile", dataInfo.getMobilePhone());
//		}
//
//		//切换车队
//		wechatUserRel.setTenantId(tenantId);
//		weChat.save(wechatUserRel);
//		SysContexts.getHttpSession(true);
//		SysContexts.setCurrentOperator(currentOperator,tokenId);
//
//		map.put("userType", currentOperator.getUserType());
//		map.put("info","Y");
//		return map;
	}

	@Override
	public Integer checkMobilePhoneForWx(String billId, Integer appCode) {

		if (!CommonUtils.isCheckPhone(billId)) {
			return 1;
		}

		SysUser sysOperator = iSysUserService.getSysOperatorByUserIdOrPhone(null, billId);
		if (sysOperator == null) {
			return 2;
		}

		UserDataInfo userDataInfo = this.getUserDataInfo(sysOperator.getUserInfoId());
		if (userDataInfo == null) {
			return 2;
		}

		if (appCode == 1) {
			if (!iServiceInfoService.isService(sysOperator.getUserInfoId())) {
				return 2;
			}
		}
		return 0;
	}

	@Override
	public void modifyPayPwd(Long userId, String oldPwd, String newPwd, Integer operType, String accessToken) {
		UserDataInfo userDataInfo = this.getUserDataInfo(userId);

		if (userDataInfo == null) {
			throw new BusinessException("用户不存在！");
		}

		//判断支付密码和登录密码是否相同
		SysUser sysOperator = iSysUserService.getSysOperatorByUserId(userId);

		try {
			if (sysOperator.getPassword() != null && sysOperator.getPassword().equals(passwordEncoder.encode(newPwd))) {
				throw new BusinessException("支付密码不能与登录密码相同");
			}
		} catch (Exception e) {
			throw new BusinessException("支付密码不能与登录密码相同");
		}

		if (operType == 1) {//设置支付密码
			//判断是否已设置支付密码
			if (userDataInfo.getAccPassword() != null && !"".equals(userDataInfo.getAccPassword())) {
				throw new BusinessException("用户已设置支付密码！");
			}
		}

		//修改支付密码，需要校验原支付密码是否正确
		if (operType == 2) {

			if (StringUtils.isEmpty(oldPwd)) {
				throw new BusinessException("旧支付密码不能为空!");
			}

//			oldPwd = EncryPwd.pwdDecryp(oldPwd);
//			if (StringUtils.isEmpty(oldPwd)) {
//				throw new BusinessException("旧支付密码不能为空!");
//			}

			String dbPayPasswd = userDataInfo.getAccPassword();

			//判断是否已设置支付密码
			if (dbPayPasswd == null || "".equals(dbPayPasswd)) {
				throw new BusinessException("未设置支付密码，请先设置！");
			}

			try {
//				if (!dbPayPasswd.equals(passwordEncoder.encode(oldPwd))) {
				if (!passwordEncoder.matches(oldPwd, dbPayPasswd)) {
					throw new BusinessException("旧支付密码错误!");
				}
			} catch (Exception e) {
				throw new BusinessException("旧支付密码错误!");
			}

			if (oldPwd.equals(newPwd)) {
				throw new BusinessException("新支付密码不能与旧支付密码相同!");
			}
		}

		try {
			userDataInfo.setAccPassword(passwordEncoder.encode(newPwd));
		} catch (Exception e) {
			e.printStackTrace();
		}
		userDataInfo.setModPwdtime(LocalDateTime.now());

		this.saveOrUpdate(userDataInfo);

		//操作日志表
		String operName = "修改支付密码";
		if (operType == 1) {
			operName = "设置支付密码";
		}

		saveSysOperLog(SysOperLogConst.BusiCode.Driver, SysOperLogConst.OperType.Update,
				userDataInfo.getId() + "," + operName, accessToken, userDataInfo.getId());

	}

    @Override
    public void payPasswdReset(Long userId, String newPayPasswd, String captcha, String billId, String loginPasswd, int channel, String accessToken) {

        //获取用户信息
        UserDataInfo userDataInfo =this.getUserDataInfo(userId);

        if (userDataInfo == null) {
            throw new BusinessException("用户不存在！");
        }


        if (userDataInfo.getUserType() != null && (userDataInfo.getUserType() == SysStaticDataEnum.USER_TYPE.DRIVER_USER ||
                userDataInfo.getUserType() == SysStaticDataEnum.USER_TYPE.CUSTOMER_USER)) {

            SysUser sysOperator = iSysUserService.getSysOperatorByUserId(userId);

            //判断支付密码和登录密码是否相同
            try {
                if (passwordEncoder.encode(newPayPasswd).equals(sysOperator.getPassword())) {
                    throw new BusinessException("支付密码不能与登录密码相同!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (channel != SysStaticDataEnum.CHANEL_TYPE.CHANEL_MINI_PROGREM) {
                if (StringUtils.isEmpty(loginPasswd)) {
                    throw new BusinessException("登录密码不能为空!");
                }

                loginPasswd = EncryPwd.pwdDecryp(loginPasswd);
                if (StringUtils.isEmpty(loginPasswd)) {
                    throw new BusinessException("登录密码不能为空!");
                }

                boolean isLoginTrue = false;
                try {
                    isLoginTrue = K.j_s(loginPasswd).equals(sysOperator.getPassword());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!isLoginTrue) {
                    throw new BusinessException("登录密码错误!");
                }
            }

        }


        try {
			userDataInfo.setAccPassword(passwordEncoder.encode(newPayPasswd));
        } catch (Exception e) {
            e.printStackTrace();
        }
        userDataInfo.setModPwdtime(LocalDateTime.now());

        this.saveOrUpdate(userDataInfo);

        String operName = ",找回支付密码";

        //记录操作日志
        saveSysOperLog(SysOperLogConst.BusiCode.Driver, SysOperLogConst.OperType.Update, userId + operName,accessToken,userId);
    }

	@Transactional
	@Override
	public void modifyLoginPhone(String billId, Long userId, String identiCode, String loginPwd, String accessToken) {
		LoginInfo loginInfo = loginUtils.get(accessToken);
		SysUser sysOperator = null;
		UserDataInfo userDataInfo = null;
		SysUser sysOperatorNew = null;

		sysOperator = iSysUserService.getSysOperatorByUserIdOrPhone(userId, null);

		if (sysOperator == null) {
			throw new BusinessException("用户不存在！");
		}

		if (sysOperator.getBillId().equals(billId)) {
			throw new BusinessException("当前用户手机与修改手机一致，修改失败！");
		}

		sysOperatorNew = iSysUserService.getSysOperatorByUserIdOrPhone(null, billId);//根据手机号查询租户
		if (sysOperatorNew != null) {
			throw new BusinessException("新手机号码已经注册，不能修改！");
		}

		//验证登路密码
		loginPwd = EncryPwd.pwdDecryption(loginPwd);
		if (StringUtils.isEmpty(loginPwd)) {
			throw new BusinessException("登录密码不能为空");
		}

		try {
			if (!K.j_s(loginPwd).equals(sysOperator.getPassword())) {
				throw new BusinessException("登录密码不正确！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		//修改操作表手机号码
		sysOperator.setBillId(billId);
		sysOperator.setLoginAcct(billId);

		iSysUserService.saveOrUpdate(sysOperator);

		//修改用户表手机号码
		userDataInfo = this.getUserDataInfo(userId);

		if (userDataInfo == null) {
			throw new BusinessException("查询用户资料失败！");
		}

		UserDataInfoVer userDataInfoVer = iUserDataInfoVerService.getUserDataInfoVerNoTenant(userDataInfo.getId());
		if (userDataInfoVer != null) {
			userDataInfoVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
			iUserDataInfoVerService.save(userDataInfoVer);
		}
		userDataInfoVer = new UserDataInfoVer();
		BeanUtil.copyProperties(userDataInfo,userDataInfoVer);
		userDataInfoVer.setMobilePhone(billId);//设置新手机号
		userDataInfoVer.setLoginName(billId);
		userDataInfoVer.setCreateDate(LocalDateTime.now());
		userDataInfoVer.setOpId(loginInfo.getId());
		userDataInfoVer.setUpdateOpId(loginInfo.getId());
		userDataInfoVer.setUpdateDate(LocalDateTime.now());
		userDataInfoVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
		iUserDataInfoVerService.save(userDataInfoVer);
		userDataInfo.setMobilePhone(billId);
		userDataInfo.setLoginName(billId);


//		if (userDataInfo.getId() != null) {
//			this.updateById(userDataInfo);
//		} else {
//			this.saveOrUpdate(userDataInfo);
//		}
		this.saveOrUpdate(userDataInfo);

		iDriverInfoExtService.resetLuGeAuthState(userDataInfo.getId());

		SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userDataInfo.getId());
		if (null != sysTenantDef) {
			sysTenantDef.setLinkPhone(billId);
			sysTenantDefService.updateById(sysTenantDef);
		}

		//操作日志表
		saveSysOperLog(SysOperLogConst.BusiCode.Driver, SysOperLogConst.OperType.Update, userId + "修改手机号", accessToken, userId);

	}

	@Override
	public void loginPasswdMng(Integer operType, String billId, String newPassword, String oldPassword, String accessToken) {
		SysUser sysOperator = iSysUserService.getSysOperatorByLoginAcct(billId, 0l);

		if (sysOperator != null) {
			//解密
			if (operType == 2) {

				if (StringUtils.isEmpty(oldPassword)) {
					throw new BusinessException("请输入旧密码!");
				}
				//解密
				oldPassword = EncryPwd.pwdDecryp(oldPassword);
				if (StringUtils.isEmpty(oldPassword)) {
					throw new BusinessException("请输入旧密码!");
				}

				try {
					if (!K.j_s(oldPassword).equals(sysOperator.getPassword())) {
						throw new BusinessException("输入的旧密码错误!");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}


				if (newPassword.equals(oldPassword)) {
					throw new BusinessException("新密码不能与旧密码相同!");
				}


				UserDataInfo userDataInfo = this.getUserDataInfo(sysOperator.getUserInfoId());

				if (userDataInfo.getAccPassword() != null) {
					try {
						if (K.j_s(newPassword).equals(userDataInfo.getAccPassword())) {
							throw new BusinessException("登录密码不能与支付密码相同!");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}

			try {
				sysOperator.setPassword(K.j_s(newPassword));
			} catch (Exception e) {
				e.printStackTrace();
			}
			iSysUserService.saveOrUpdate(sysOperator);

		} else {
			throw new BusinessException("手机号对应的用户不存在!");
		}

		String operName = ",设置登录密码";

		if (operType == 2) {
			operName = ",修改登录密码";
		}

		//操作日志表
		saveSysOperLog(SysOperLogConst.BusiCode.Driver, SysOperLogConst.OperType.Update,
				sysOperator.getId() + operName + "", accessToken, sysOperator.getId());
	}

    @Override
    public void loginPasswdReset(String billId, String captcha, String newPassword) {
        SysUser sysOperator = iSysUserService.getSysOperatorByLoginAcct(billId, 0L);

        if (sysOperator != null) {

            UserDataInfo userDataInfo = this.getUserDataInfo(sysOperator.getUserInfoId());

            if (userDataInfo.getAccPassword() != null) {
                try {
                    if (K.j_s(newPassword).equals(userDataInfo.getAccPassword())) {
                        throw new BusinessException("登录密码不能与支付密码相同!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                sysOperator.setPassword(K.j_s(newPassword));
            } catch (Exception e) {
                e.printStackTrace();
            }
            iSysUserService.saveOrUpdate(sysOperator);

        } else {
            throw new BusinessException("手机号对应的用户不存在!");
        }

    }

	@Override
	public Page<StaffDataInfoDto> queryStaffInfo(StaffDataInfoVo vo, Integer pageNum, Integer pageSize, String accessToken) {
		LoginInfo loginInfo = loginUtils.get(accessToken);
		Page<StaffDataInfoDto> staffDataInfoDtoPage = baseMapper.queryStaffInfoPage(new Page<StaffDataInfoDto>(pageNum, pageSize), vo, loginInfo.getTenantId());
		//循环查询绑定银行卡情况,需求变更就是这样
		List<StaffDataInfoDto> items = staffDataInfoDtoPage.getRecords();
		for (StaffDataInfoDto item : items) {
			if (iAccountBankRelService.isUserTypeBindCardAll(item.getUserId(), SysStaticDataEnum.USER_TYPE.CUSTOMER_USER)) {
				item.setBankBindState(EnumConsts.BIND_STATE.BIND_STATE_ALL);
			}
		}
		staffDataInfoDtoPage.setRecords(items);
		return staffDataInfoDtoPage;
	}

	@Override
	public List<StaffDataInfoDto> getUserInfoByUserName(StaffDataInfoVo vo, String accessToken) {
		LoginInfo loginInfo = loginUtils.get(accessToken);
		List<StaffDataInfoDto> staffDataInfoDtos = userDataInfoRecordMapper.queryStaffInfo(vo, loginInfo.getTenantId());
		//循环查询绑定银行卡情况,需求变更就是这样
		for (StaffDataInfoDto item : staffDataInfoDtos) {
			if (iAccountBankRelService.isUserTypeBindCardAll(item.getUserId(), SysStaticDataEnum.USER_TYPE.CUSTOMER_USER)) {
				item.setBankBindState(EnumConsts.BIND_STATE.BIND_STATE_ALL);
			}
		}
		return staffDataInfoDtos;
	}

	@Override
	public AuthInfoDto queryAuthInfo(Long userId) {

		//查询用户以及账户信息
		UserDataInfo userDataInfo = this.getUserDataInfo(userId);

		if (userDataInfo == null) {
			throw new BusinessException("查找用户资料失败！");
		}

		FastDFSHelper client = null;
		try {
			client = FastDFSHelper.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		AuthInfoDto dto = new AuthInfoDto();
		int state = 1;
		//state 认证状态  1、未认证 2、已认证 3、认证失败，请重新认证  这个需要转换
		state = userDataInfo.getAuthState() == null ? 1 : userDataInfo.getAuthState();
		dto.setState(state);
		dto.setStateName(getSysStaticData("USER_STATE", String.valueOf(state)).getCodeName());

		if (org.apache.commons.lang.StringUtils.isNotEmpty(userDataInfo.getVerifReason())) {
			dto.setState(3);
			dto.setStateName("认证失败，请重新认证");
		}
		dto.setVerifReason(userDataInfo.getVerifReason());

		if (userDataInfo.getAuthState() != null && userDataInfo.getAuthState() == SysStaticDataEnum.AUTH_STATE.AUTH_STATE1) {
			dto.setFirstFlg(1);
		} else {
			dto.setFirstFlg(0);
		}

		if (userDataInfo.getTenantId() != null && userDataInfo.getTenantId() > 0) {
			TenantUserRel tenantUserRel = iTenantUserRelService.getAllTenantUserRelByUserId(userId, userDataInfo.getTenantId());
			if (tenantUserRel != null) {
				dto.setState(tenantUserRel.getState());
				state = tenantUserRel.getState();
				dto.setStateName(getSysStaticData("USER_STATE", String.valueOf(tenantUserRel.getState())).getCodeName());

				if (org.apache.commons.lang.StringUtils.isNotEmpty(tenantUserRel.getStateReason())) {
					dto.setState(3);
					dto.setStateName("认证失败，请重新认证");
				}
				dto.setVerifReason(tenantUserRel.getStateReason());
			}
		}
		dto.setUserName(userDataInfo.getLinkman());
		dto.setLoginAcct(userDataInfo.getMobilePhone());
		dto.setIdentification(userDataInfo.getIdentification());
		dto.setAdriverLicenseSn(userDataInfo.getAdriverLicenseSn());
		dto.setIdenPictureFront(userDataInfo.getIdenPictureFront());
		try {
			dto.setIdenPictureFrontUrl(client.getHttpURL(userDataInfo.getIdenPictureFrontUrl()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		dto.setIdenPictureBack(userDataInfo.getIdenPictureBack());
		try {
			dto.setIdenPictureBackUrl(client.getHttpURL(userDataInfo.getIdenPictureBackUrl()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		dto.setAdriverLicenseOriginal(userDataInfo.getAdriverLicenseOriginal());
		try {
			dto.setAdriverLicenseOriginalUrl(client.getHttpURL(userDataInfo.getAdriverLicenseOriginalUrl()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		dto.setAdriverLicenseDuplicate(userDataInfo.getAdriverLicenseDuplicate());
		try {
			dto.setAdriverLicenseDuplicateUrl(client.getHttpURL(userDataInfo.getAdriverLicenseDuplicateUrl()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		dto.setQcCerti(userDataInfo.getQcCerti());
		try {
			dto.setQcCertiUrl(client.getHttpURL(userDataInfo.getQcCertiUrl()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		dto.setVehicleType(userDataInfo.getVehicleType());
		dto.setDriverLicenseTime(userDataInfo.getDriverLicenseTime());
		dto.setDriverLicenseExpiredTime(userDataInfo.getDriverLicenseExpiredTime());
		dto.setQcCertiTime(userDataInfo.getQcCertiTime());
		dto.setQcCertiExpiredTime(userDataInfo.getQcCertiExpiredTime());
		if (state == 1) {
			UserDataInfoVer userDataInfoVerOld = iUserDataInfoVerService.getUserDataInfoVer(userDataInfo.getId());
			if (userDataInfoVerOld != null) {
				dto.setUserName(userDataInfoVerOld.getLinkman());
				dto.setLoginAcct(userDataInfoVerOld.getMobilePhone());
				dto.setIdentification(userDataInfoVerOld.getIdentification());
				dto.setAdriverLicenseSn(userDataInfoVerOld.getAdriverLicenseSn());
				dto.setIdenPictureFront(userDataInfoVerOld.getIdenPictureFront());
				try {
					dto.setIdenPictureFrontUrl(client.getHttpURL(userDataInfoVerOld.getIdenPictureFrontUrl()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				dto.setIdenPictureBack(userDataInfoVerOld.getIdenPictureBack());
				try {
					dto.setIdenPictureBackUrl(client.getHttpURL(userDataInfoVerOld.getIdenPictureBackUrl()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				dto.setAdriverLicenseOriginal(userDataInfoVerOld.getAdriverLicenseOriginal());
				try {
					dto.setAdriverLicenseOriginalUrl(client.getHttpURL(userDataInfoVerOld.getAdriverLicenseOriginalUrl()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				dto.setAdriverLicenseDuplicate(userDataInfoVerOld.getAdriverLicenseDuplicate());
				try {
					dto.setAdriverLicenseDuplicateUrl(client.getHttpURL(userDataInfoVerOld.getAdriverLicenseDuplicateUrl()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				dto.setQcCerti(userDataInfoVerOld.getQcCerti());
				try {
					dto.setQcCertiUrl(client.getHttpURL(userDataInfoVerOld.getQcCertiUrl()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				dto.setVehicleType(userDataInfoVerOld.getVehicleType());
				dto.setDriverLicenseTime(userDataInfo.getDriverLicenseTime());
				dto.setDriverLicenseExpiredTime(userDataInfo.getDriverLicenseExpiredTime());
				dto.setQcCertiTime(userDataInfo.getQcCertiTime());
				dto.setQcCertiExpiredTime(userDataInfo.getQcCertiExpiredTime());
			}
		}

		Boolean bindCardEver = iAccountBankRelService.isUserBindCardEver(userId);
		dto.setBindCardEver(bindCardEver ? 1 : 0);

		return dto;
	}

	@Override
	public void saveCarAuthInfo(CarAuthInfoVo vo,String accessToken) {
		Boolean ocrFlag = vo.getOcrFlag();
		//校验身份证是否已经存在
		iUserService.checkExistIdentification(vo.getIdentification(), vo.getUserId());

		UserDataInfo userDataInfo = this.getUserDataInfo(vo.getUserId());
		if (userDataInfo == null) {
			throw new BusinessException("查找用户资料失败！");
		}
		String identificationOld = userDataInfo.getIdentification();
		String linkmanOld = userDataInfo.getLinkman();

		SysUser sysOperator = iSysUserService.getSysOperatorByUserIdOrPhone(vo.getUserId(), "");
		if (sysOperator == null) {
			throw new BusinessException("查找用户操作资料失败！");
		}

		sysOperator.setOpName(vo.getUserName());
		userDataInfo.setLinkman(vo.getUserName());
		iSysUserService.saveOrUpdate(sysOperator);
		SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(sysOperator.getUserInfoId(), true);
		if (null != sysTenantDef) {
			sysTenantDef.setLinkMan(vo.getUserName());
			sysTenantDefService.update(sysTenantDef);
		}

		//把所有审核记录置为失效
		List<UserDataInfoVer> userDataInfoVerList = iUserDataInfoVerService.getUserDataInfoVerList(userDataInfo.getId(), SysStaticDataEnum.VER_STATE.VER_STATE_Y);
		if (userDataInfoVerList != null && !userDataInfoVerList.isEmpty()) {
			for (UserDataInfoVer ver : userDataInfoVerList) {
				ver.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
				iUserDataInfoVerService.updateById(ver);
			}
		}

		Integer oldHasVer = userDataInfo.getHasVer();

		//平台车直接修改为待审核，否则需要处理司机关系表
		if (userDataInfo.getTenantId() != null && userDataInfo.getTenantId() > 0 && userDataInfo.getTenantId() != SysStaticDataEnum.PT_TENANT_ID) {
			List<TenantUserRel> tenantUserRels = iTenantUserRelService.getTenantUserRelListByUserId(vo.getUserId(), userDataInfo.getTenantId());
			if (tenantUserRels != null && !tenantUserRels.isEmpty()) {
				TenantUserRel tenantUserRel = tenantUserRels.get(0);

				//所有记录记为不可审
				List<TenantUserRelVer> tenantUserRelVers = iTenantUserRelVerService.getTenantUserRelVerByRelId(tenantUserRel.getId());
				if (tenantUserRelVers != null && !tenantUserRelVers.isEmpty()) {
					for (TenantUserRelVer ver : tenantUserRelVers) {
						ver.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
						iTenantUserRelVerService.saveOrUpdate(ver);
					}
				}

				//从新复制ver表
				ocrFlag = false;
				TenantUserRelVer tenantUserRelVer = new TenantUserRelVer();
				BeanUtil.copyProperties(tenantUserRel, tenantUserRelVer);
				tenantUserRel.setHasVer(EnumConsts.HAS_VER_STATE.HAS_VER_NO); //未审
				tenantUserRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y); //可审
				BeanUtil.copyProperties(vo, tenantUserRelVer);

				iTenantUserRelVerService.saveOrUpdate(tenantUserRelVer);
				iTenantUserRelService.saveOrUpdate(tenantUserRel);

				if (tenantUserRel.getState() == SysStaticDataEnum.AUTH_STATE.AUTH_STATE1) {
					BeanUtil.copyProperties(tenantUserRel, tenantUserRelVer);
					iTenantUserRelService.saveOrUpdate(tenantUserRel);
				}

				//启动审核流程
				Map map = new HashMap();
				map.put("updateFlg", "Y1");//只修改司机信息
				map.put("updateIdentification", vo.getIdentification().equals(identificationOld) ? "N" : "Y");
				Boolean bool = iAuditService.startProcess(AuditConsts.AUDIT_CODE.AUDIT_CODE_USER, tenantUserRel.getId(), SysOperLogConst.BusiCode.Driver, map, accessToken, userDataInfo.getTenantId());
				if (!bool) {
					throw new BusinessException("启动审核流程失败！");
				}
			}
		} else {
			userDataInfo.setHasVer(EnumConsts.HAS_VER_STATE.HAS_VER_NO);
		}

		//ver表
		UserDataInfoVer userDataInfoVer = new UserDataInfoVer();
		BeanUtil.copyProperties(userDataInfo, userDataInfoVer);
		BeanUtil.copyProperties(vo, userDataInfoVer);

		//改下路径
		String trackerHost = ClientGlobal.getG_secret_key() + "/";
		vo.setIdenPictureFrontUrl(vo.getIdenPictureFrontUrl());
		userDataInfoVer.setIdenPictureFrontUrl(vo.getIdenPictureFrontUrl());
		vo.setIdenPictureBackUrl(vo.getIdenPictureBackUrl());
		userDataInfoVer.setIdenPictureBackUrl(vo.getIdenPictureBackUrl());
		vo.setAdriverLicenseOriginalUrl(vo.getAdriverLicenseOriginalUrl());
		userDataInfoVer.setAdriverLicenseOriginalUrl(vo.getAdriverLicenseOriginalUrl());
		vo.setAdriverLicenseDuplicateUrl(vo.getAdriverLicenseDuplicateUrl());
		userDataInfoVer.setAdriverLicenseDuplicateUrl(vo.getAdriverLicenseDuplicateUrl());
		if (vo.getQcCerti() != null && vo.getQcCerti() > 0) {
			userDataInfoVer.setQcCertiUrl(vo.getQcCertiUrl());
		}
		userDataInfoVer.setHasVer(oldHasVer);
		userDataInfoVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);

		//写日志
		saveSysOperLog(SysOperLogConst.BusiCode.Driver, SysOperLogConst.OperType.Update, "司机认证资料保存", accessToken, userDataInfo.getId());

		if (ocrFlag != null && ocrFlag) {
			if (!vo.getIdentification().equals(identificationOld) || !vo.getUserName().equals(linkmanOld)) {
				iDriverInfoExtService.resetLuGeAuthState(userDataInfo.getId());
			}
			userDataInfoVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
			BeanUtil.copyProperties(userDataInfoVer, userDataInfo);
			userDataInfo.setHasVer(EnumConsts.HAS_VER_STATE.HAS_VER_YES);
			userDataInfo.setAuthState(SysStaticDataEnum.SYS_USER_STATE.AUDIT_APPROVE);

			//写日志
			saveSysOperLog(SysOperLogConst.BusiCode.DriverOBMS, SysOperLogConst.OperType.Audit, "司机认证资料自动审核(OCR)", accessToken, userDataInfo.getId(), SysStaticDataEnum.PT_TENANT_ID);
		} else {
			//纯c断司机 触发自动审核
			if (userDataInfo.getTenantId() == null || userDataInfo.getTenantId() <= 0 || userDataInfo.getTenantId() == SysStaticDataEnum.PT_TENANT_ID) {
				boolean isAutoAudit = SysCfgUtil.getCfgBooleanVal("IS_AUTO_AUDIT", accessToken, loginUtils, redisUtil);
				if (isAutoAudit) {

					if (!vo.getIdentification().equals(identificationOld) || !vo.getUserName().equals(linkmanOld)) {
						iDriverInfoExtService.resetLuGeAuthState(userDataInfo.getId());
					}

					userDataInfoVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
					BeanUtil.copyProperties(userDataInfoVer, userDataInfo);//
					userDataInfo.setHasVer(EnumConsts.HAS_VER_STATE.HAS_VER_YES);
					userDataInfo.setAuthState(SysStaticDataEnum.SYS_USER_STATE.AUDIT_APPROVE); //1未认证，2已认证

					//写日志
					saveSysOperLog(SysOperLogConst.BusiCode.DriverOBMS, SysOperLogConst.OperType.Audit, "司机认证资料自动审核", accessToken, userDataInfo.getId(), SysStaticDataEnum.PT_TENANT_ID);
				}
			}

			if (userDataInfo.getAuthState() != null && userDataInfo.getAuthState() == SysStaticDataEnum.AUTH_STATE.AUTH_STATE1) {//1未认证，2已认证,3认证失败 ?????一会这个一会那个？???
				BeanUtil.copyProperties(userDataInfoVer, userDataInfo);
				this.save(userDataInfo);
			}
		}
		iUserDataInfoVerService.save(userDataInfoVer);

//		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//		if (StringUtils.isNotBlank(vo.getDriverLicenseTime())) {
//			userDataInfo.setDriverLicenseTime(LocalDateTime.parse(vo.getDriverLicenseTime() + " 00:00:00", df));
//		}
//		if (StringUtils.isNotBlank(vo.getDriverLicenseExpiredTime())) {
//			userDataInfo.setDriverLicenseExpiredTime(LocalDateTime.parse(vo.getDriverLicenseExpiredTime() + " 00:00:00", df));
//		}
//		if (StringUtils.isNotBlank(vo.getQcCertiTime())) {
//			userDataInfo.setQcCertiTime(LocalDateTime.parse(vo.getQcCertiTime() + " 00:00:00", df));
//		}
//		if (StringUtils.isNotBlank(vo.getQcCertiExpiredTime())) {
//			userDataInfo.setQcCertiExpiredTime(LocalDateTime.parse(vo.getQcCertiExpiredTime() + " 00:00:00", df));
//		}
//		userDataInfo.setIdenPictureFront(vo.getIdenPictureFront());
//		userDataInfo.setIdenPictureFrontUrl(vo.getIdenPictureFrontUrl());
//		userDataInfo.setIdenPictureBack(vo.getIdenPictureBack());
//		userDataInfo.setIdenPictureBackUrl(vo.getIdenPictureBackUrl());
//		userDataInfo.setAdriverLicenseOriginal(vo.getAdriverLicenseOriginal());
//		userDataInfo.setAdriverLicenseOriginalUrl(vo.getAdriverLicenseOriginalUrl());
//		userDataInfo.setAdriverLicenseDuplicate(vo.getAdriverLicenseDuplicate());
//		userDataInfo.setAdriverLicenseDuplicateUrl(vo.getAdriverLicenseDuplicateUrl());
//		userDataInfo.setQcCerti(vo.getQcCerti());
//		userDataInfo.setQcCertiUrl(vo.getQcCertiUrl());
	//	this.saveOrUpdate(userDataInfo);

	}

	private List<Map> getTenantListMap(List<TenantStaffRel> tenantStaffRelList) {
		List<Map> tenantList = new ArrayList<>();
		for (TenantStaffRel tenantStaffRel : tenantStaffRelList) {
			SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(tenantStaffRel.getTenantId());
			Map tenantMap = new HashMap();
			tenantMap.put(sysTenantDef.getId(), sysTenantDef.getName());
			tenantList.add(tenantMap);
		}
		return tenantList;
	}

	@Override
	public UserDataInfo getUserDataInfoByMoblile(String mobilePhone, boolean tenantFlag,String accessToken) {
	 	QueryWrapper<UserDataInfo> queryWrapper=new QueryWrapper<>();
	 	queryWrapper.eq("mobile_Phone",mobilePhone);
	 	if(tenantFlag){
	 		queryWrapper.eq("tenant_Id",this.getLoginInfoByAccessToken(accessToken).getTenantId());
		}
		List<UserDataInfo> users=baseMapper.selectList(queryWrapper);
		UserDataInfo userInfo = null;
		if(users != null && users.size() > 0){
			userInfo = users.get(0);
		}
		return userInfo;
	}

	@Override
	public void overrideMobilePhoneAndLinkman(Long userId, String mobilePhone, String linkman) {
		SysUser sysOperator = iSysUserService.getSysUserByUserIdOrPhone(userId, null);
		if (sysOperator != null ){
			if(!mobilePhone.equals(sysOperator.getLoginAcct())) {
				sysOperator.setBillId(mobilePhone);
				sysOperator.setLoginAcct(mobilePhone);
				iSysUserService.save(sysOperator);
			}
			if(StringUtils.isNotEmpty(linkman)
					&&!linkman.equals(sysOperator.getName())){
				sysOperator.setName(linkman);
				iSysUserService.updateById(sysOperator);
				QueryWrapper<TenantStaffRel> queryWrapper=new QueryWrapper<>();
				queryWrapper.eq("user_id",userId);
				List<TenantStaffRel> tenantStaffRelList = iTenantStaffRelService.list(queryWrapper);
				if(tenantStaffRelList!=null&&!tenantStaffRelList.isEmpty()){
					for (TenantStaffRel tenantStaffRel : tenantStaffRelList) {
						tenantStaffRel.setStaffName(linkman);
						iTenantStaffRelService.updateStaffInfo(tenantStaffRel);
					}
				}
				SysTenantDef tenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userId);
				if (null != tenantDef) {
					tenantDef.setLinkMan(linkman);
					sysTenantDefService.updateById(tenantDef);
				}
			}

		}

	}

	@Override
	public Page<UserDataInfoBackVo> queryAllTenantDriverOrPtDriver(Page<UserDataInfoBackVo> page, String linkman, String loginAcct, String accessToken) {
		LoginInfo loginInfo = loginUtils.get(accessToken);
		Page<UserDataInfoBackVo> userDataInfoBackVoPage = baseMapper.queryAllTenantDriverOrPtDriver(page, linkman, loginAcct, loginInfo.getTenantId());
		List<UserDataInfoBackVo> records = userDataInfoBackVoPage.getRecords();
		if(records != null &&records.size() >0){
			for (UserDataInfoBackVo record : records) {
				if(record.getCarUserType() != null){
					record.setCarUserTypeName(readisUtil.getSysStaticData("DRIVER_TYPE",record.getCarUserType().toString()).getCodeName());
				}else{
					record.setCarUserTypeName("平台司机");
					record.setState(2);
				}
			}

			userDataInfoBackVoPage.setRecords(records);
		}
		return userDataInfoBackVoPage;
	}

	public void modifyUserName(Long userId, String linkman) {
		List<TenantStaffRel> list = iTenantStaffRelService.getTenantStaffRel(userId);
		for (TenantStaffRel rel : list) {
			rel.setStaffName(linkman);
		}

		SysTenantDef tenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userId);
		if (null != tenantDef) {
			tenantDef.setLinkMan(linkman);
			sysTenantDefService.updateById(tenantDef);
		}

		SysUser sysUser = iSysUserService.getByUserInfoId(userId);
		sysUser.setOpName(linkman);
		iSysUserService.updateById(sysUser);
	}

	/**
	 * 记录日志
	 */
	private void saveSysOperLog(SysOperLogConst.BusiCode busiCode, SysOperLogConst.OperType operType, String operCommet, String accessToken, Long busid) {
		SysOperLog operLog = new SysOperLog();
		operLog.setBusiCode(busiCode.getCode());
		operLog.setBusiName(busiCode.getName());
		operLog.setBusiId(busid);
		operLog.setOperType(operType.getCode());
		operLog.setOperTypeName(operType.getName());
		operLog.setOperComment(operCommet);
		sysOperLogService.save(operLog, accessToken);
	}

	private void saveSysOperLog(SysOperLogConst.BusiCode busiCode, SysOperLogConst.OperType operType, String operCommet, String accessToken, Long busid, Long tenantId) {
		SysOperLog operLog = new SysOperLog();
		operLog.setBusiCode(busiCode.getCode());
		operLog.setBusiName(busiCode.getName());
		operLog.setBusiId(busid);
		operLog.setOperType(operType.getCode());
		operLog.setOperTypeName(operType.getName());
		operLog.setOperComment(operCommet);
		sysOperLogService.save(operLog, tenantId, accessToken);
	}

	@Override
	public UserDataInfo selectUserType(Long userInfoId) {
		UserDataInfo userDataInfo = userDataInfoRecordMapper.selectById(userInfoId);
		return userDataInfo;
	}

	public SysStaticData getSysStaticData(String codeType, String codeValue) {
		List<SysStaticData> staticDataList = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
		if (staticDataList != null && staticDataList.size() > 0) {
			Iterator var3 = staticDataList.iterator();
			while (var3.hasNext()) {
				SysStaticData sysData = (SysStaticData) var3.next();
				if (sysData.getCodeValue().equals(codeValue)) {
					return sysData;
				}
			}
		}
		return new SysStaticData();
	}

}
