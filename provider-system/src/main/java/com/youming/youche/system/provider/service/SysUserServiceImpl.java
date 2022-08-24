package com.youming.youche.system.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.cloud.api.sms.ISysSmsSendService;
import com.youming.youche.cloud.domin.sms.SysSmsSend;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.SysCfg;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.encrypt.K;
import com.youming.youche.finance.domain.payable.SysOperator;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.record.api.tenant.ITenantUserRelService;
import com.youming.youche.record.api.tenant.IUserReceiverInfoService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.ITenantStaffRelService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.domain.UserType;
import com.youming.youche.system.dto.UserTypeDto;
import com.youming.youche.system.provider.mapper.SysUserMapper;
import com.youming.youche.system.utils.GainAuthCodeUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
@DubboService(version = "1.0.0")
public class SysUserServiceImpl extends BaseServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

	private static final int codeTypes[] = new int[]{1, 2, 3, 11, 17};

	@Resource
	RedisUtil redisUtil;

	@Resource
	private IUserDataInfoService userDataInfoService;
    @Resource
    private PasswordEncoder passwordEncoder;

	@DubboReference(version = "1.0.0")
	ISysSmsSendService iSysSmsSendService;

	@Resource
	ISysTenantDefService sysTenantDefService;

	@Resource
	ITenantStaffRelService tenantStaffRelService;

	@Resource
	ITenantUserRelService tenantUserRelService;

	@DubboReference(version = "1.0.0")
	IServiceInfoService serviceInfoService;

	@DubboReference(version = "1.0.0")
	IUserReceiverInfoService userReceiverInfoService;
	@Override
	public SysUser get(String accessToken) {
		SysUser sysUser = (SysUser) redisUtil.get("user:" + accessToken);
		return sysUser;
	}

	@Override
	public SysUser getByUserInfoId(Long userInfoId) {
		LambdaQueryWrapper<SysUser> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(SysUser::getUserInfoId, userInfoId);
		return baseMapper.selectOne(wrapper);
	}

//	@Override
//	public SysUser getSysOperatorByUserIdOrPhone(Long userId, String billId, Long tenantId) {
//		LambdaQueryWrapper<SysUser> lambda=Wrappers.lambdaQuery();
//		lambda.gt(SysUser::getState, SysStaticDataEnum.SYS_STATE_DESC.STATE_NO)
//				.ne(SysUser::getState,SysStaticDataEnum.VERIFY_STS.VIRTUAL_USERS);
//		if (userId != null && userId > 0) {
//			lambda.eq(SysUser::getId, userId);
//		} else {
//			lambda.eq(SysUser::getBillId, billId);
//		}
//		List<SysUser> sysOperatorList = this.list(lambda);
//		if (sysOperatorList == null || sysOperatorList.isEmpty()) {
//			return null;
//		}
//		SysUser sysOperator = sysOperatorList.get(0);
//		return sysOperator;
//	}

	@Override
	public Long saveSysUser(SysUser sysUser) {
		this.save(sysUser);
        return sysUser.getId();
	}


	@Override
	public long saveServerChildUser(String billId, Long userId, String password, String linkman) {
		UserDataInfo userDataInfo = null;
		//保存operator
		SysUser sysOperator = null;
		if(userId>0){
			userDataInfo = userDataInfoService.get(userId);

			if(userDataInfo==null){
				throw new BusinessException("用户信息不存在");
			}
			getSysOperatorByUserIdOrPhone(userId,null);

			if(sysOperator==null){
				throw new BusinessException("该手机号码不存在");
			}
//			if(userDataInfo.getUserType()!=SysStaticDataEnum.USER_TYPE.SERVER_CHILD_USER){
//				throw new BusinessException("用户类型不正确");
//			}
		}else {
			sysOperator=getSysOperatorByUserIdOrPhone(null,billId);
			if(sysOperator!=null){
				throw new BusinessException("该手机号码已经存在");
			}
			sysOperator = new SysUser();
			//保存user_data_info
			userDataInfo = new UserDataInfo();
			userDataInfo.setTenantId(-1L);
		}

		userDataInfo.setMobilePhone(billId);
		userDataInfo.setLoginName(linkman);
		userDataInfo.setLinkman(linkman);
		userDataInfo.setSourceFlag(SysStaticDataEnum.SOURCE_FLAG.PLATFORM);//数据来源：0，平台
		userDataInfo.setUserType(SysStaticDataEnum.USER_TYPE.SERVER_CHILD_USER);
		userDataInfo.setAuthState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE2);
		userDataInfoService.save(userDataInfo);


		//保存sysuser
		sysOperator.setBillId(billId);
		sysOperator.setLoginAcct(billId);
		sysOperator.setUserInfoId(userDataInfo.getId());
		sysOperator.setOpName(linkman);//姓名
		sysOperator.setLockFlag(SysStaticDataEnum.LOCK_FLAG.LOCK_YES);
//		sysOperator.setPassword(K.j_s(password));
		sysOperator.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
		save(sysOperator);


		return sysOperator.getUserInfoId();
	}

	@Override
	public SysUser getSysOperatorByUserIdOrPhone(Long userId, String billId) {
		LambdaQueryWrapper<SysUser> lambda=new QueryWrapper<SysUser>().lambda();
		lambda.gt(SysUser::getState, SysStaticDataEnum.SYS_STATE_DESC.STATE_NO)
				.ne(SysUser::getState,SysStaticDataEnum.VERIFY_STS.VIRTUAL_USERS);
		if(userId!=null && userId>0){
			lambda.eq(SysUser::getUserInfoId,userId);
		}else{
			lambda.eq(SysUser::getBillId,billId);
		}
		List<SysUser> sysUserList = this.list(lambda);
		if(sysUserList==null || sysUserList.size() ==0){
			return null;
		}
		SysUser sysUser = sysUserList.get(0);
		return sysUser;
	}

	@Override
	public SysUser getSysOperatorByUserDatainfoIdOrPhone(Long userId, String billId) {
		LambdaQueryWrapper<SysUser> lambda=new QueryWrapper<SysUser>().lambda();
		lambda.gt(SysUser::getState, SysStaticDataEnum.SYS_STATE_DESC.STATE_NO)
				.ne(SysUser::getState,SysStaticDataEnum.VERIFY_STS.VIRTUAL_USERS);
		if(userId!=null && userId>0){
			lambda.eq(SysUser::getUserInfoId,userId);
		}
		if(StringUtils.isNotEmpty(billId)){
			lambda.eq(SysUser::getBillId,billId);
		}
		List<SysUser> sysUserList = this.list(lambda);
		if(sysUserList==null || sysUserList.size() ==0){
			return null;
		}
		SysUser sysUser = sysUserList.get(0);
		return sysUser;
	}


	@Override
	public SysUser isExistUserInfo(Long userId, String billId) {

		QueryWrapper<SysUser> sysOperatorQueryWrapper = new QueryWrapper<>();

		if(userId!=null&&userId>0){
			sysOperatorQueryWrapper.eq("user_id",userId);
		}else{
			sysOperatorQueryWrapper.eq("bill_id",billId);
		}
		List<SysUser> sysOperatorList = baseMapper.selectList(sysOperatorQueryWrapper);
		SysUser sysOperator=null;
		if(sysOperatorList.size()>0){
			sysOperator= sysOperatorList.get(0);
			if(sysOperator.getState()== SysStaticDataEnum.VERIFY_STS.VIRTUAL_USERS){
				sysOperator=null;
			}
		}
		return sysOperator;
	}

	@Override
	public SysUser getSysOperatorByUserId(Long userId) {
		LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(SysUser::getUserInfoId,userId);

		return this.getOne(queryWrapper);
	}

	@Override
	public SysUser getSysOperatorByUserIdOrPhone(Long userId, String billId, Long tenantId) {
		LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.gt(SysUser::getState,SysStaticDataEnum.SYS_STATE_DESC.STATE_NO);
		queryWrapper.ne(SysUser::getState,SysStaticDataEnum.VERIFY_STS.VIRTUAL_USERS);
		if(userId != null && userId > 0){
			queryWrapper.eq(SysUser::getUserInfoId,userId);
		}else{
			queryWrapper.eq(SysUser::getBillId, billId);
		}

		List<SysUser> list = this.list(queryWrapper);
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	@Override
	public void valideLoginPwdForUpdatePhone(Long userId, String loginPwd) {
		SysUser sysOperator = this.getSysOperatorByUserIdOrPhone(userId, null, 0l);

		if (sysOperator == null) {
			throw new BusinessException("用户不存在！");
		}

		try {
			if(!passwordEncoder.matches(loginPwd,sysOperator.getPassword())){
				throw new BusinessException("登录密码不正确！");
			}
//			if (!K.j_s(loginPwd).equals(sysOperator.getPassword())) {
//
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public List<SysUser> getSysUserAll() {
		LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.isNotNull(SysUser::getTenantId);
		return baseMapper.selectList(queryWrapper);
	}

	@Override
	public void getMessageAuthCode(String billId, Integer codeType) {
		if (StringUtils.isEmpty(billId)) {
			throw new BusinessException("请输入获取手机号码!");
		}
		if (!this.isCheckPhone(billId)) {
			throw new BusinessException("请输入正确的手机号码!");
		}
		if (codeType <= 0) {
			throw new BusinessException("请输入短信验证码类型!");
		}
		if (!this.isContains(codeTypes, codeType)) {
			throw new BusinessException("请输入有效的验证码类型!");
		}
		Long tenantId = -1L;
		SysUser sysOperator = this.getSysOperatorByUserIdOrPhone(null, billId);
		if (codeType == SysStaticDataEnum.SYS_CODE_TYPE.REGISTER_CODE) {

		} else if (codeType == SysStaticDataEnum.SYS_CODE_TYPE.REGISTER_CODE_PHONE) {

		} else if (codeType == SysStaticDataEnum.SYS_CODE_TYPE.LOGIN_RESET_CODE ||
				codeType == SysStaticDataEnum.SYS_CODE_TYPE.PAY_CODE ||
				codeType == SysStaticDataEnum.SYS_CODE_TYPE.LOGIN_CODE) {
			if (sysOperator == null) {
				throw new BusinessException("该用户未注册，获取验证码失败");
			}
			tenantId = sysOperator.getTenantId();
		}

		String alidkey = GainAuthCodeUtil.getValidValue(codeType + "");
		Long templateId = GainAuthCodeUtil.getTemplateValue(alidkey);

		SysSmsSend sysSmsSend = new SysSmsSend();
		sysSmsSend.setTemplateId(templateId);
		sysSmsSend.setBillId(billId);
		sysSmsSend.setSmsType(SysStaticDataEnum.SMS_TYPE.MESSAGE_AUTHENTICATION);
		sysSmsSend.setObjType(SysStaticDataEnum.OBJ_TYPE.NOTIFY + "");
		sysSmsSend.setTenantId(tenantId);
		sysSmsSend.setParamMap(new HashMap<String, String>());
		iSysSmsSendService.sendSms(sysSmsSend);

	}

	/**
	 * 检测正确的手机号码
	 */
	private Boolean isCheckPhone(String billId) {

		if (billId != null && !billId.equals("")) {
			SysCfg sysCfg = (SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(EnumConsts.SysCfg.CHK_PHONE));
			if (sysCfg != null && !sysCfg.getCfgValue().equals("")) {
				String codeValue = sysCfg.getCfgValue();
				Pattern pat = Pattern.compile(codeValue);
				Matcher mat = pat.matcher(billId);
				if (mat.matches()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断数组是否包含该数字
	 *
	 * @param numbers 数组
	 * @param value   判断数字
	 *                true 包含 false不包含
	 */
	private Boolean isContains(int[] numbers, int value) {
		boolean flag = false;
		OUT:
		for (int i : numbers) {
			if (i == value) {
				flag = true;
				break OUT;
			}
		}
		return flag;
	}

	@Override
	public List<UserType> getUserType(long userId, Long tenantId) {
		UserTypeDto out = getUserTypeOut(userId, tenantId);
		List<UserType> result = new ArrayList<>();

		if (out.isAdminUser()) {
			result.add(new UserType(6, "超级管理员"));
		}
		if (out.isStaff()) {
			result.add(new UserType(1, "员工"));
		}
		if (out.isDriver()) {
			result.add(new UserType(3, "司机"));
		}
		if (out.isService()) {
			result.add(new UserType(2, "服务商"));
		}
		if (out.isReceiver()) {
			result.add(new UserType(7, "收款人"));
		}
		return result;
	}

	@Override
	public List<SysUser> getPhoneUser(String phone) {
		LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		lambdaQueryWrapper.eq(SysUser::getLoginAcct,phone);
		return super.list(lambdaQueryWrapper);
	}

	private UserTypeDto getUserTypeOut(long userId, Long tenantId) {
		UserTypeDto out = new UserTypeDto();
		out.setAdminUser(sysTenantDefService.isAdminUser(userId, tenantId));
		out.setStaff(tenantStaffRelService.isStaff(userId, tenantId));
		out.setDriver(tenantUserRelService.isDriver(userId, tenantId));
		out.setService(serviceInfoService.isService(userId, tenantId));
		out.setReceiver(userReceiverInfoService.isReceiver(userId, tenantId));
		return out;
	}
}
