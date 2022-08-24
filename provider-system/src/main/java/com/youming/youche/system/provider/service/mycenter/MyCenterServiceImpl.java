package com.youming.youche.system.provider.service.mycenter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.cloud.api.sms.ISysSmsSendService;
import com.youming.youche.cloud.domin.sms.SysSmsSend;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.IWechatUserRelService;
import com.youming.youche.system.api.mycenter.IMyCenterService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.dto.mycenter.SysUserDto;
import com.youming.youche.system.provider.mapper.mycenter.MyCenterMapper;
import com.youming.youche.system.vo.mycenter.UpdatePhoneVo;
import com.youming.youche.util.SysMagUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static com.youming.youche.conts.EnumConsts.SmsTemplate.REGIST_CODE;
import static com.youming.youche.conts.EnumConsts.SmsTemplate.RESET_CODE;
import static com.youming.youche.conts.EnumConsts.SmsTemplate.UPDATE_STAFF_PHONE;


/**
 * @ClassName MyCenterServiceImpl
 * @Description 添加描述
 * @Author zag
 * @Date 2022/2/18 17:06
 */
@DubboService(version = "1.0.0")
public class MyCenterServiceImpl implements IMyCenterService {

    private static final Logger log = LoggerFactory.getLogger(MyCenterServiceImpl.class);

    @DubboReference(version = "1.0.0")
    ISysSmsSendService sysSmsSendService;

    @Autowired
    IUserDataInfoService userDataInfoService;

    @Autowired
    ISysUserService sysUserService;

    @Autowired
    ISysTenantDefService sysTenantDefService;

    @Autowired
    MyCenterMapper myCenterMapper;

    @Resource
    RedisUtil redisUtil;

    @Resource
    IWechatUserRelService wechatUserRelService;

    private final String REDIS_VERIFY_CODE_PACKET = "verify_code:";


    @Override
    public SysUserDto getSysUserById(Long userId, Long tenantId) {
        return myCenterMapper.seletcSysUserById(userId, tenantId);
    }

    @Override
    public boolean updatePwd(Long userId, String newPwd) {
        return myCenterMapper.updatePwd(userId, newPwd) > 0 ? true : false;
    }

    @Override
    public boolean resetPwd(String phone, String newPwd) {
        LambdaQueryWrapper<SysUser> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysUser::getLoginAcct, phone);
        SysUser sysUser = sysUserService.getOne(queryWrapper);
        if (sysUser == null) {
            throw new BusinessException("该用户帐号未注册");
        }
        return updatePwd(sysUser.getUserInfoId(), newPwd);
    }

    @Override
    public void sendVerifyCodeByUpdatePhone(Long userId, String phone, Boolean isNewPhone) {
        LambdaQueryWrapper<UserDataInfo> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(UserDataInfo::getMobilePhone, phone);
        UserDataInfo userDataInfo = userDataInfoService.getOne(queryWrapper);
        if (isNewPhone) {
            if (userDataInfo != null) {
                throw new BusinessException("手机号码已存在，请更换手机号码");
            } else {
                userDataInfo = userDataInfoService.getById(userId);
            }
        }
        //次数限制key
        String key = REDIS_VERIFY_CODE_PACKET + EnumConsts.SmsTemplate.UPDATE_STAFF_PHONE + "_" + phone;
        //验证码key
        String codeKey = REDIS_VERIFY_CODE_PACKET + EnumConsts.SmsTemplate.UPDATE_STAFF_PHONE + "_" + phone + "_CODE";

        int count = 0;
        if (redisUtil.hasKey(key)) {
            count = Integer.valueOf(redisUtil.get(key).toString());
        }

        if (count > 5) {
            throw new BusinessException("24小时内只能发送5次，你已超出限制");
        }

        //生产验证码，两分钟有效
        String randomCode = SysMagUtil.getRandomNumber(6);
        boolean redisResult = redisUtil.setex(codeKey, randomCode, 120);
        if (redisResult) {
            log.info("员工更换手机号码：redis缓存手机验证码成功，key:" + codeKey + ";value:" + randomCode + ";time:120");
        }

        Map<String, String> paraMap = new HashMap<String, String>();
        paraMap.put("code", randomCode);
        paraMap.put("name", userDataInfo.getLinkman());
        SysSmsSend sysSmsSend = new SysSmsSend();
        sysSmsSend.setBillId(phone);
        sysSmsSend.setSmsType(SysStaticDataEnum.SMS_TYPE.MESSAGE_AUTHENTICATION);
        sysSmsSend.setTemplateId(EnumConsts.SmsTemplate.UPDATE_STAFF_PHONE);
        sysSmsSend.setObjType(String.valueOf(SysStaticDataEnum.OBJ_TYPE.NOTIFY));
        sysSmsSend.setParamMap(paraMap);
        sysSmsSendService.sendSms(sysSmsSend);

        //发送短信验证码成功后次数加1
        count++;
        redisResult = redisUtil.setex(key, String.valueOf(count), 24 * 60 * 60);
        if (redisResult) {
            log.info("员工更换手机号码：redis缓存手机验证码发送次数，key:" + key + ";value:" + count + ";time:24 * 60 * 60");
        }
    }

    @Override
    public void sendVerifyCodeByResetPwd(String phone) {
        LambdaQueryWrapper<SysUser> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysUser::getLoginAcct, phone);
        SysUser sysUser = sysUserService.getOne(queryWrapper);
        if (sysUser == null) {
            throw new BusinessException("该用户帐号未注册");
        }
        //限制次数
        String key = REDIS_VERIFY_CODE_PACKET + REGIST_CODE + "_" + phone;
        String codeKey = REDIS_VERIFY_CODE_PACKET + REGIST_CODE + "_" + phone + "_CODE";

        int count = 0;

        if (redisUtil.hasKey(key)) {
            count = Integer.valueOf(redisUtil.get(key).toString());
        }

        if (count >= 5) {
            throw new BusinessException("24小时内只能发送5次，你已超出限制");
        }

        //生产验证码，两分钟有效
        String randomCode = SysMagUtil.getRandomNumber(6);
        boolean redisResult = redisUtil.setex(codeKey, randomCode, 120);
        if (redisResult) {
            log.info("注册验证码：redis缓存手机验证码成功，key:" + codeKey + ";value:" + randomCode + ";time:120");
        }

        Map<String, String> paraMap = new HashMap<String, String>();
        paraMap.put("code", randomCode);
        SysSmsSend sysSmsSend = new SysSmsSend();
        sysSmsSend.setBillId(phone);
        sysSmsSend.setSmsType(SysStaticDataEnum.SMS_TYPE.MESSAGE_AUTHENTICATION);
        sysSmsSend.setTemplateId(RESET_CODE);
        sysSmsSend.setObjType(String.valueOf(SysStaticDataEnum.OBJ_TYPE.NOTIFY));
        sysSmsSend.setParamMap(paraMap);
        sysSmsSendService.sendSms(sysSmsSend);

        //发送短信验证码成功后次数加1
        count++;
        redisResult = redisUtil.setex(key, String.valueOf(count), 24 * 60 * 60);
        if (redisResult) {
            log.info("注册验证码：redis缓存手机验证码发送次数，key:" + key + ";value:" + count + ";time:24 * 60 * 60");
        }
    }


    @Override
    public boolean checkVerifyCodeByUpdatePhone(String phone, String verifyCode) {
        String codeKey = REDIS_VERIFY_CODE_PACKET + UPDATE_STAFF_PHONE + "_" + phone + "_CODE";
        return checkVerifyCode(codeKey, verifyCode);
    }

    @Override
    public boolean checkVerifyCodeByResetPwd(String phone, String verifyCode) {
        String codeKey = REDIS_VERIFY_CODE_PACKET + REGIST_CODE + "_" + phone + "_CODE";
        return checkVerifyCode(codeKey, verifyCode);
    }

    private boolean checkVerifyCode(String codeKey, String verifyCode) {
        if (redisUtil.hasKey(codeKey)) {
            String randomCode = redisUtil.get(codeKey).toString();
            if (null != randomCode && randomCode.equals(verifyCode)) {
                return true;
            }
        }
        return false;
    }


    @Override
    @Transactional
    public boolean updatePhone(UpdatePhoneVo updatePhoneVo) {
        boolean checkResult = checkVerifyCodeByUpdatePhone(updatePhoneVo.getNewPhone(), updatePhoneVo.getVerifyCode());
        if (!checkResult) {
            throw new BusinessException("新手机验证码不正确");
        }
        LambdaQueryWrapper<UserDataInfo> userDataInfoLambdaQueryWrapper = Wrappers.lambdaQuery();
        userDataInfoLambdaQueryWrapper.eq(UserDataInfo::getMobilePhone, updatePhoneVo.getOldPhone());
        UserDataInfo userDataInfo = userDataInfoService.getOne(userDataInfoLambdaQueryWrapper);
        String sourcePhone = "";
        if (null != userDataInfo) {
            sourcePhone = userDataInfo.getMobilePhone();
            userDataInfo.setMobilePhone(updatePhoneVo.getNewPhone());
            userDataInfo.setLoginName(updatePhoneVo.getNewPhone());
            userDataInfoService.update(userDataInfo);
        }

        LambdaQueryWrapper<SysUser> sysUserLambdaQueryWrapper = Wrappers.lambdaQuery();
        sysUserLambdaQueryWrapper.eq(SysUser::getUserInfoId, userDataInfo.getId());
        SysUser sysUser = sysUserService.getOne(sysUserLambdaQueryWrapper);
        if (null != sysUser) {
            sysUser.setBillId(updatePhoneVo.getNewPhone());
            sysUser.setLoginAcct(updatePhoneVo.getNewPhone());
            sysUserService.update(sysUser);
        }

        LambdaQueryWrapper<SysTenantDef> sysTenantDefLambdaQueryWrapper = Wrappers.lambdaQuery();
        sysTenantDefLambdaQueryWrapper.eq(SysTenantDef::getAdminUser, userDataInfo.getId());
        SysTenantDef sysTenantDef = sysTenantDefService.getOne(sysTenantDefLambdaQueryWrapper);
        if (null != sysTenantDef) {
            sysTenantDef.setLinkPhone(updatePhoneVo.getNewPhone());
            sysTenantDefService.update(sysTenantDef);
        }
        try {
            //维修解绑
            wechatUserRelService.unbindPhone(sourcePhone);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

}
