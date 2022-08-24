package com.youming.youche.record.provider.service.impl.sys;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.encrypt.K;
import com.youming.youche.record.api.sys.ISysUserService;
import com.youming.youche.record.api.user.IUserDataInfoRecordService;
import com.youming.youche.record.common.EncryPwd;
import com.youming.youche.record.common.SysStaticDataEnum;
import com.youming.youche.record.provider.mapper.sys.SysUserMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-20
 */
@DubboService(version = "1.0.0")
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {


    @Lazy
    @Autowired
    IUserDataInfoRecordService iUserDataInfoRecordService;


    @Override
    public SysUser getSysUserByUserId(Long userId) {
        QueryWrapper<SysUser> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_info_id",userId);
        List<SysUser> list=baseMapper.selectList(queryWrapper);
        if(list!=null && list.size()>0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public SysUser insertSysUser(String loginAcct, Long userId, String operatorName, Integer lockFlag,
                                         String pwd, Long tenantId, Integer verifyStatus, String tenantCode,String accessToken) {
        LoginInfo user= iUserDataInfoRecordService.getLoginInfoByAccessToken(accessToken);
        Long opId=user.getId();
        SysUser sysUser = new SysUser();
        sysUser.setTenantId(tenantId);
        sysUser.setBillId(loginAcct);
        Instant instant = new Date().toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        sysUser.setLoginAcct(loginAcct);
        sysUser.setUserInfoId(userId);
        sysUser.setName(operatorName);
        sysUser.setOpUserId(opId);
        sysUser.setLockFlag(lockFlag);
        sysUser.setState(verifyStatus);
        sysUser.setTenantCode(tenantCode);
        sysUser.setCreateTime(localDateTime);
        if (null != pwd) {
            try {
                sysUser.setPassword(K.j_s(EncryPwd.pwdDecryption(pwd)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.save(sysUser);
        return sysUser;
    }

    @Override
    public SysUser getSysUserByUserIdOrPhone(Long userId, String billId)  {
        QueryWrapper<SysUser> queryWrapper=new QueryWrapper<>();
        queryWrapper.gt("state", SysStaticDataEnum.SYS_STATE_DESC.STATE_NO);
        queryWrapper.ne("state",SysStaticDataEnum.VERIFY_STS.VIRTUAL_USERS);
        if(userId!=null&&userId>0){
           queryWrapper.eq("user_info_id", userId);
        }else{
            queryWrapper.eq("bill_Id", billId);
        }
        List<SysUser> sysUserList = baseMapper.selectList(queryWrapper);
        if(sysUserList==null||sysUserList.isEmpty()){
            return null;
        }
        SysUser SysUser = sysUserList.get(0);
        return SysUser;
    }

    @Override
    public SysUser getByUserInfoId(Long userInfoId) {
        LambdaQueryWrapper<SysUser> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysUser::getUserInfoId, userInfoId);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public SysUser getSysOperatorByUserIdOrPhone(Long userId, String billId) {
        LambdaQueryWrapper<SysUser> lambda = new QueryWrapper<SysUser>().lambda();
        lambda.gt(SysUser::getState, SysStaticDataEnum.SYS_STATE_DESC.STATE_NO)
                .ne(SysUser::getState, SysStaticDataEnum.VERIFY_STS.VIRTUAL_USERS);
        if (userId != null && userId > 0) {
            lambda.eq(SysUser::getUserInfoId, userId);
        } else {
            lambda.eq(SysUser::getBillId, billId);
        }
        List<SysUser> sysUserList = this.list(lambda);
        if (sysUserList == null || sysUserList.size() == 0) {
            return null;
        }
        SysUser sysUser = sysUserList.get(0);
        return sysUser;
    }

    @Override
    public SysUser getSysOperatorByUserId(Long userId) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUserInfoId,userId);

        return this.getOne(queryWrapper);
    }

    @Override
    public SysUser getSysOperatorByLoginAcct(String loginAcct, long tenantId) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getLoginAcct, loginAcct);
        List<SysUser> list = this.list(queryWrapper);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return new SysUser();
    }

}
