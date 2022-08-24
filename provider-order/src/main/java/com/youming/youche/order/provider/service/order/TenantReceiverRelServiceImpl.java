package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.order.ITenantReceiverRelService;
import com.youming.youche.order.domain.order.TenantReceiverRel;
import com.youming.youche.order.provider.mapper.order.TenantReceiverRelMapper;
import com.youming.youche.record.api.tenant.IUserReceiverInfoService;
import com.youming.youche.record.domain.user.UserReceiverInfo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.UserDataInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


/**
 * <p>
 * 车队与收款人的关联关系 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@DubboService(version = "1.0.0")
@Service
public class TenantReceiverRelServiceImpl extends BaseServiceImpl<TenantReceiverRelMapper, TenantReceiverRel> implements ITenantReceiverRelService {

    @Autowired
    private ITenantReceiverRelService tenantReceiverRelService;
    @DubboReference(version = "1.0.0")
    private IUserDataInfoService userDataInfoService;
    @DubboReference(version = "1.0.0")
    private IUserReceiverInfoService userReceiverInfoService;

    @DubboReference(version = "1.0.0")
    private ISysOperLogService sysOperLogService;
    @DubboReference(version = "1.0.0")
    private ISysUserService sysUserService;

    @Override
    public TenantReceiverRel createTenantReceiverRel(Long userReceiverId, String remark, Long tenantId, LoginInfo user) {
        if (null == tenantId || tenantId < 0) {
            throw new BusinessException("参数错误");
        }
        TenantReceiverRel tenantReceiverRel = tenantReceiverRelService.getTenantReceiverRel(userReceiverId, tenantId);
        if (null != tenantReceiverRel) {
            return tenantReceiverRel;
        }

        tenantReceiverRel = new TenantReceiverRel();
        tenantReceiverRel.setReceiverId(userReceiverId);
        tenantReceiverRel.setTenantId(tenantId);
        LocalDateTime now = LocalDateTime.now();
        tenantReceiverRel.setCreateTime(now);
        tenantReceiverRel.setRemark(remark);
        tenantReceiverRel.setUpdateTime(now);
        tenantReceiverRelService.save(tenantReceiverRel);

        return tenantReceiverRel;
    }


    @Override
    public TenantReceiverRel getTenantReceiverRel(long receiverId, long tenantId) {
        LambdaQueryWrapper<TenantReceiverRel> lambda= Wrappers.lambdaQuery();
        lambda.eq(TenantReceiverRel::getReceiverId,receiverId)
              .eq(TenantReceiverRel::getTenantId,tenantId);
        return this.getOne(lambda);
    }

    @Override
    public void createUserReceiverInfo(String phone, String receiverName, String linkman, String remark,
                                       Long tenantId,LoginInfo user) {
        if (null == tenantId) {
            tenantId = user.getTenantId();
        }

        UserDataInfo userDataInfo = userDataInfoService.getPhone(phone);
        if (null == userDataInfo) {
            userDataInfo = initUserDataInfo(phone, linkman,user);
        }

        UserReceiverInfo userReceiverInfo = userReceiverInfoService.getUserReceiverInfoByUserId(userDataInfo.getId());
        if (null == userReceiverInfo || userReceiverInfo.getId() == null) {
            userReceiverInfo = initUserReceiverInfo(receiverName, userDataInfo.getId(),user);
        } else {
            userReceiverInfo.setReceiverName(receiverName);
            userReceiverInfo.setUpdateTime(LocalDateTime.now());
            userReceiverInfoService.update(userReceiverInfo);
        }

        TenantReceiverRel rel = createTenantReceiverRel(userReceiverInfo, remark, tenantId);

        sysOperLogService.saveSysOperLog(user,SysOperLogConst.BusiCode.UserReceivcer,
                rel.getId(),SysOperLogConst.OperType.Add,"新增收款人");
    }

    private UserReceiverInfo initUserReceiverInfo(String receiverName, Long userId,LoginInfo user) {
        UserReceiverInfo userReceiverInfo = new UserReceiverInfo();
        userReceiverInfo.setReceiverName(receiverName);
        userReceiverInfo.setOpId(user.getId());
        userReceiverInfo.setCreateTime(LocalDateTime.now());
        userReceiverInfo.setUpdateTime(LocalDateTime.now());
        userReceiverInfo.setUserId(userId);

        return userReceiverInfoService.saveReturnId(userReceiverInfo);
    }
    private UserDataInfo initUserDataInfo(String phone, String linkman,LoginInfo user)  {
        UserDataInfo userDataInfo = new UserDataInfo();
        userDataInfo.setLinkman(linkman);
        userDataInfo.setMobilePhone(phone);
        userDataInfo.setSourceFlag(0);
        UserDataInfo info = userDataInfoService.saveReturnId(userDataInfo);


        SysUser sysOperator = new SysUser();
        sysOperator.setTenantId(null);
        sysOperator.setBillId(phone);
        sysOperator.setCreateTime(LocalDateTime.now());
        sysOperator.setLoginAcct(phone);
        sysOperator.setUserInfoId( info.getId());
        sysOperator.setName(linkman);
        sysOperator.setOpUserId(user.getId());
        sysOperator.setLockFlag(1);
        sysOperator.setState(SysStaticDataEnum.VERIFY_STS.UNAUTHORIZED);
        sysOperator.setTenantCode(null);

        sysUserService.saveSysUser(sysOperator);

        return info;
    }
    /**
     *  初始化一个收款人（不会为当前车队建立关系表）
     *  如果手机号码已存在与系统中，直接使用对应的用户。
     *  如果手机号码已经是收款人，直接返回已有记录
     */
    public UserReceiverInfo initUserReceiverInfo(String phone, String receiverName,LoginInfo user) throws Exception {
        UserDataInfo userDataInfo = userDataInfoService.getPhone(phone);
        if (null == userDataInfo) {
            userDataInfo = initUserDataInfo(phone, receiverName,user);
        }

        UserReceiverInfo userReceiverInfo = userReceiverInfoService.getUserReceiverInfoByUserId(userDataInfo.getId());
        if (null == userReceiverInfo) {
            return initUserReceiverInfo(receiverName, userDataInfo.getId(),user);
        } else {
            return userReceiverInfo;
        }
    }

    private TenantReceiverRel createTenantReceiverRel(UserReceiverInfo userReceiverInfo, String remark, Long tenantId) {
        if (null == tenantId || tenantId < 0) {
            throw new BusinessException("参数错误");
        }
        TenantReceiverRel tenantReceiverRel = this.getTenantReceiverRel(userReceiverInfo.getId(), tenantId);
        if (null != tenantReceiverRel) {
            return tenantReceiverRel;
        }

        tenantReceiverRel = new TenantReceiverRel();
        tenantReceiverRel.setReceiverId(userReceiverInfo.getId());
        tenantReceiverRel.setTenantId(tenantId);
        tenantReceiverRel.setCreateTime(LocalDateTime.now());
        tenantReceiverRel.setRemark(remark);
        tenantReceiverRel.setUpdateTime(LocalDateTime.now());
        this.save(tenantReceiverRel);

        return tenantReceiverRel;
    }

}
