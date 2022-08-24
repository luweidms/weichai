package com.youming.youche.market.provider.service.facilitator;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.facilitator.IUserDataInfoMarketService;
import com.youming.youche.market.api.facilitator.base.ServiceUserService;
import com.youming.youche.market.domain.facilitator.ServiceInfo;
import com.youming.youche.market.domain.facilitator.UserDataInfo;
import com.youming.youche.market.domain.facilitator.UserDataInfoVer;
import com.youming.youche.market.dto.facilitator.ServiceSaveInDto;
import com.youming.youche.market.dto.facilitator.ServiceUserInDto;
import com.youming.youche.market.provider.mapper.facilitator.ServiceInfoMapper;
import com.youming.youche.market.provider.mapper.facilitator.UserDataInfoVerMapper;
import com.youming.youche.system.api.ISysUserService;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
@DubboService(version = "1.0.0")
@Service
public class ServiceUserServiceImpl  implements ServiceUserService {
    @Autowired
    private IUserDataInfoMarketService userDataInfoService;
    @Resource
    private BCryptPasswordEncoder passwordEncoder;
    @DubboReference(version = "1.0.0")
    private ISysUserService sysUserService;
    @Resource
    private UserDataInfoVerMapper userDataInfoVerMapper;
    @Resource
    private ServiceInfoMapper serviceInfoMapper;
    @Override
    public HashMap<String, Object> saveServiceUser(ServiceUserInDto serviceUserIn, LoginInfo baseUser, ServiceSaveInDto serviceSaveIn) {
        HashMap<String, Object> result = new HashMap<>();
        //保存user_data_info
        UserDataInfo userDataInfo = new UserDataInfo();
        userDataInfo.setTenantId(-1L);
        userDataInfo.setMobilePhone(serviceUserIn.getLoginAcct());
        userDataInfo.setLoginName(serviceUserIn.getLoginAcct());
        userDataInfo.setLinkman(serviceUserIn.getLinkman());
        userDataInfo.setSourceFlag(0);//数据来源：0，平台
        userDataInfo.setUserType(SysStaticDataEnum.USER_TYPE.SERVICE_USER);
        userDataInfo.setUserPrice(serviceUserIn.getUserPrice());
        userDataInfo.setUserPriceUrl(serviceUserIn.getUserPriceUrl());
        userDataInfo.setIdentification(serviceUserIn.getIdentification());
        userDataInfo.setIdenPictureFront(serviceUserIn.getIdenPictureFront());
        userDataInfo.setIdenPictureFrontUrl(serviceUserIn.getIdenPictureFrontUrl());
        userDataInfo.setIdenPictureBack(serviceUserIn.getIdenPictureBack());
        userDataInfo.setIdenPictureBackUrl(serviceUserIn.getIdenPictureBackUrl());
        if(serviceUserIn.getAuthFlg()==0) {
            userDataInfo.setAuthState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE2);
            Long userId = userDataInfoService.saveUserDataInfo(userDataInfo);
            userDataInfo.setId(userId);
        }else{
            userDataInfo.setAuthState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE1);
            userDataInfo.setHasVer(EnumConsts.HAS_VER_STATE.HAS_VER_NO);
            Long userId = userDataInfoService.saveUserDataInfo(userDataInfo);
            userDataInfo.setId(userId);
            UserDataInfoVer userDataInfoVer = new UserDataInfoVer();
            BeanUtils.copyProperties(userDataInfo,userDataInfoVer);
            userDataInfoVer.setId(userDataInfo.getId());
            userDataInfoVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
            userDataInfoVerMapper.insert(userDataInfoVer);
        }
        result.put("userDataInfo",userDataInfo);

        //保存operator
        SysUser sysOperator = new SysUser();
        sysOperator.setBillId(serviceUserIn.getLoginAcct());
        sysOperator.setCreateTime(LocalDateTime.now());
        sysOperator.setLoginAcct(serviceUserIn.getLoginAcct());
        sysOperator.setId(userDataInfo.getId());
        sysOperator.setName(serviceUserIn.getLinkman());
        sysOperator.setUserInfoId(userDataInfo.getId());
        sysOperator.setTenantId(baseUser.getTenantId());
        sysOperator.setUserType(serviceSaveIn.getServiceType());
        SysUser user = sysUserService.getById(baseUser.getId());
        if(user!=null){
            sysOperator.setOpName(user.getName());//姓名
        }
        sysOperator.setName(serviceUserIn.getLinkman());
        sysOperator.setLockFlag(SysStaticDataEnum.LOCK_FLAG.LOCK_YES);
        if(StringUtils.isNotEmpty(serviceUserIn.getPassword())) {
            sysOperator.setPassword(passwordEncoder.encode(serviceUserIn.getPassword()));
        }
        sysOperator.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        Long opId = sysUserService.saveSysUser(sysOperator);
        sysOperator.setOpUserId(opId);
        result.put("operator", sysOperator);
        return result;
    }

    /**
     * 保存和新增
     * @param serviceInfo
     * @param isUpdate
     */
    @Override
    public void doSaveOrUpdate(ServiceInfo serviceInfo, boolean isUpdate, LoginInfo baseUser) {
        if(isUpdate){
            serviceInfo.setUpdateTime(LocalDateTime.now());
            serviceInfo.setOpId(baseUser.getId());
            serviceInfoMapper.updateById(serviceInfo);
        }else {
            serviceInfo.setCreateTime(LocalDateTime.now());
            serviceInfo.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
            serviceInfo.setTenantId(baseUser.getTenantId());
            serviceInfoMapper.insert(serviceInfo);
        }

    }
}
