package com.youming.youche.record.provider.service.impl.tenant;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.order.ITenantReceiverRelService;
import com.youming.youche.record.api.account.IAccountBankRelService;
import com.youming.youche.record.api.sys.ISysUserService;
import com.youming.youche.record.api.tenant.IUserReceiverInfoService;
import com.youming.youche.record.api.user.IUserDataInfoRecordService;
import com.youming.youche.record.common.EnumConsts;
import com.youming.youche.record.domain.tenant.TenantReceiverRel;
import com.youming.youche.record.domain.user.UserDataInfo;
import com.youming.youche.record.domain.user.UserReceiverInfo;
import com.youming.youche.record.dto.tenant.TenantReceiverRelDto;
import com.youming.youche.record.provider.mapper.tenant.UserReceiverInfoMapper;
import com.youming.youche.record.provider.mapper.user.UserDataInfoRecordMapper;
import com.youming.youche.record.vo.tenant.TenantReceiverRelVo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysStaticDataService;
import com.youming.youche.system.domain.SysOperLog;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@DubboService(version = "1.0.0")
public class UserReceiverInfoServiceImpl extends BaseServiceImpl<UserReceiverInfoMapper, UserReceiverInfo> implements IUserReceiverInfoService {

    @Resource
    private LoginUtils loginUtils;


    @Autowired
    IUserDataInfoRecordService iUserDataInfoRecordService;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;
    @Lazy
    @Autowired
    ISysUserService iSysUserService;

    @Resource
    UserDataInfoRecordMapper userDataInfoRecordMapper;

    @Resource
    IAccountBankRelService accountBankRelService;

    @DubboReference(version = "1.0.0")
    ISysStaticDataService isysStaticDataService;

    @Resource
    IUserReceiverInfoService userReceiverInfoService;

    @DubboReference(version = "1.0.0")
    ITenantReceiverRelService tenantReceiverRelService;


    @Override
    public Page<TenantReceiverRelDto> queryAll(Page<TenantReceiverRelDto> page, String accessToken, TenantReceiverRelVo tenantReceiverRelVo) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        baseMapper.queryAll(page, loginInfo.getTenantId(), tenantReceiverRelVo);
        //获取绑定银行卡信息
        if (page != null && page.getRecords() != null && page.getRecords().size() > 0) {
            for (TenantReceiverRelDto tenantReceiverRelDto : page.getRecords()
            ) {
                if(tenantReceiverRelDto.getUserId() != null) {
                    if (accountBankRelService.isUserTypeBindCardAll(tenantReceiverRelDto.getUserId(), null)) {
                        tenantReceiverRelDto.setBindState(EnumConsts.BIND_STATE.BIND_STATE_ALL);
                        tenantReceiverRelDto.setBindStateName(isysStaticDataService.getSysStaticData("BIND_STATE", EnumConsts.BIND_STATE.BIND_STATE_ALL + "").getCodeName());
                    }else{
                        tenantReceiverRelDto.setBindState(EnumConsts.BIND_STATE.BIND_STATE_NONE);
                        tenantReceiverRelDto.setBindStateName(isysStaticDataService.getSysStaticData("BIND_STATE", EnumConsts.BIND_STATE.BIND_STATE_NONE + "").getCodeName());
                    }
                }
            }
        }
        return page;
    }

    @Override
    public TenantReceiverRel contractById(Long relId) {
        return baseMapper.ContractById(relId);
    }


    @Override
    public boolean tenantAdd(TenantReceiverRel tenantReceiverRel, String accessToken) throws Exception {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        UserDataInfo userDataInfo = userDataInfoRecordMapper.queryUserInfoByMobile(tenantReceiverRel.getMobilePhone());
        if (null == userDataInfo) {
            userDataInfo = initUserDataInfo(tenantReceiverRel.getMobilePhone(), tenantReceiverRel.getLinkman(),loginInfo);
        }
        LambdaQueryWrapper<UserReceiverInfo> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(UserReceiverInfo::getUserId, userDataInfo.getId()).orderByDesc(UserReceiverInfo::getCreateTime);
        List<UserReceiverInfo> userReceiverInfos = super.list(lambdaQueryWrapper);
        UserReceiverInfo userReceiverInfo = new UserReceiverInfo();
        if (null == userReceiverInfos || userReceiverInfos.size() == 0) {
            userReceiverInfo = initUserReceiverInfo(tenantReceiverRel.getReceiverName(), userDataInfo.getId(), accessToken);
        } else {
            userReceiverInfo = userReceiverInfos.get(0);
            userReceiverInfo.setReceiverName(tenantReceiverRel.getReceiverName());
            userReceiverInfo.setUpdateTime(LocalDateTime.now());
            super.update(userReceiverInfo);
            super.update(userReceiverInfo);
        }
        TenantReceiverRel tenantReceiverRel1 = new TenantReceiverRel();
        tenantReceiverRel1.setReceiverId(userReceiverInfo.getId());
        tenantReceiverRel1.setCreateTime(userReceiverInfo.getCreateTime());
        tenantReceiverRel1.setUpdateTime(userReceiverInfo.getUpdateTime());
        tenantReceiverRel1.setRemark(tenantReceiverRel.getRemark());
        tenantReceiverRel1.setTenantId(loginInfo.getTenantId());
        tenantReceiverRel1.setUserId(loginInfo.getUserInfoId());
        baseMapper.createTenantReceiverRel(tenantReceiverRel1);
        saveSysOperLog(SysOperLogConst.BusiCode.UserReceivcer, SysOperLogConst.OperType.Add, "新增收款人", accessToken, tenantReceiverRel1.getId());
        return true;

    }


    @Override
    public boolean tenantUpdate(TenantReceiverRel tenantReceiverRels, String accessToken) {
    //    SysUser sysUser = getSysUser(accessToken);
        TenantReceiverRel tenantReceiverRel = baseMapper.ContractById(tenantReceiverRels.getId());
        if (null == tenantReceiverRel) {
            throw new BusinessException("不存在的收款人");
        }
        UserReceiverInfo userReceiverInfo = super.getById(tenantReceiverRel.getReceiverId());
        if (null == userReceiverInfo) {
            throw new BusinessException("数据错误");
        }
        userReceiverInfo.setReceiverName(tenantReceiverRels.getReceiverName());
        userReceiverInfo.setUpdateTime(LocalDateTime.now());
        super.update(userReceiverInfo);
        tenantReceiverRel.setRemark(tenantReceiverRels.getRemark());
        tenantReceiverRel.setUpdateTime(LocalDateTime.now());
        baseMapper.updateBytenant(tenantReceiverRel);
        saveSysOperLog(SysOperLogConst.BusiCode.UserReceivcer, SysOperLogConst.OperType.Update, "修改收款人", accessToken, tenantReceiverRels.getId());
        return true;
    }

    @Override
    public boolean deleteTeantReceiverById(Long relid) {
        return baseMapper.deleteTeantReceiverById(relid);
    }

    @Override
    public TenantReceiverRel checkUserReceiver(String mobilePhone, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        UserDataInfo userDataInfo = userDataInfoRecordMapper.getUserDataInfoByMoblile(mobilePhone);
        if (null == userDataInfo) {
            return null;
        }
        TenantReceiverRel result = new TenantReceiverRel();
        result.setLinkman(userDataInfo.getLinkman());
        result.setUserId(userDataInfo.getId());

        UserReceiverInfo userReceiverInfo = getUserReceiverInfoByUserId(userDataInfo.getId());
        if (null != userReceiverInfo) {
            result.setReceiverName(userReceiverInfo.getReceiverName());
            TenantReceiverRel tenantReceiverRel = baseMapper.getTenantReceiverRel(userReceiverInfo.getId(), loginInfo.getTenantId());
            if (null != tenantReceiverRel) {
                result.setId(tenantReceiverRel.getId());
            }
        }

        return result;
    }

    @Override
    public UserReceiverInfo initUserReceiverInfo(String mobilePhone, String receiverName, String token) {
        LoginInfo loginInfo = loginUtils.get(token);
        UserDataInfo userDataInfo = userDataInfoRecordMapper.getUserDataInfoByMoblile(mobilePhone);
        if (null == userDataInfo) {
            userDataInfo = initUserDataInfo(mobilePhone, receiverName,loginInfo);
        }

        UserReceiverInfo userReceiverInfo = getUserReceiverInfoByUserId(userDataInfo.getId());
        if (null == userReceiverInfo) {
            return initUserReceiverInfo(receiverName, userDataInfo.getId(), token);
        } else {
            return userReceiverInfo;
        }
    }

    @Override
    public UserReceiverInfo initUserReceiverInfo(String mobilePhone, String receiverName, LoginInfo loginInfo) {
        UserDataInfo userDataInfo = userDataInfoRecordMapper.getUserDataInfoByMoblile(mobilePhone);
        if (null == userDataInfo) {
            userDataInfo = initUserDataInfo(mobilePhone, receiverName,loginInfo);
        }

        UserReceiverInfo userReceiverInfo = getUserReceiverInfoByUserId(userDataInfo.getId());
        if (null == userReceiverInfo) {
            return initUserReceiverInfo(receiverName, userDataInfo.getId(), loginInfo);
        } else {
            return userReceiverInfo;
        }
    }

    @Override
    public Boolean isReceiver(Long userId, Long tenantId) {
        if (null == tenantId) {
            return null != getUserReceiverInfoByUserId(userId);
        }
        return CollectionUtils.isNotEmpty(getTenantReceiverRelByUserId(userId, tenantId));
    }

    @Override
    public UserReceiverInfo getUserReceiverInfoByUserId(long userId) {
        LambdaQueryWrapper<UserReceiverInfo> lambda = Wrappers.lambdaQuery();
        lambda.eq(UserReceiverInfo::getUserId, userId);
        List<UserReceiverInfo> userReceiverInfoList = super.list(lambda);
        if (userReceiverInfoList != null && userReceiverInfoList.size() > 0) {
            return userReceiverInfoList.get(0);
        }
        return new UserReceiverInfo();
    }

    @Override
    public void updateUserReceiverInfo(UserReceiverInfo userReceiverInfo) {
        super.updateById(userReceiverInfo);
    }

    @Override
    public UserReceiverInfo getUserReceiverInfoById(Long receiverId) {
        return super.getById(receiverId);
    }

    @Override
    public UserReceiverInfo saveReturnId(UserReceiverInfo userReceiverInfo) {
        this.save(userReceiverInfo);
        return userReceiverInfo;
    }

    @Override
    public UserReceiverInfo getUserReceiverInfo(String phone, String accessToken) {
        UserDataInfo userDataInfo = iUserDataInfoRecordService.getUserDataInfoByMoblile(phone, false, accessToken);
        if (null == userDataInfo) {
            return null;
        }

        UserReceiverInfo userReceiverInfo = this.getUserReceiverInfoByUserId(userDataInfo.getId());
        if (null != userReceiverInfo) {
            userReceiverInfo.setUserDataInfo(userDataInfo);
        }

        return userReceiverInfo;
    }


    public List<TenantReceiverRel> getTenantReceiverRelByUserId(long userId, Long tenantId) {
        UserReceiverInfo userReceiverInfo = getUserReceiverInfoByUserId(userId);
        if (null == userReceiverInfo) {
            return null;
        }
        return baseMapper.getTenantReceiverRelByUserId(userId, tenantId);
    }

    private UserReceiverInfo initUserReceiverInfo(String receiverName, Long id, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        UserReceiverInfo userReceiverInfo = new UserReceiverInfo();
        userReceiverInfo.setReceiverName(receiverName);
        userReceiverInfo.setOpId(loginInfo.getOrgIds().get(0));
        userReceiverInfo.setCreateTime(LocalDateTime.now());
        userReceiverInfo.setUpdateTime(LocalDateTime.now());
        userReceiverInfo.setUserId(id);
        super.save(userReceiverInfo);
        //     userReceiverInfoMapper.add(userReceiverInfo);
        return userReceiverInfo;
    }

    private UserReceiverInfo initUserReceiverInfo(String receiverName, Long id, LoginInfo loginInfo) {
        UserReceiverInfo userReceiverInfo = new UserReceiverInfo();
        userReceiverInfo.setReceiverName(receiverName);
        userReceiverInfo.setOpId(loginInfo.getOrgIds().get(0));
        userReceiverInfo.setCreateTime(LocalDateTime.now());
        userReceiverInfo.setUpdateTime(LocalDateTime.now());
        userReceiverInfo.setUserId(id);
        super.save(userReceiverInfo);
        return userReceiverInfo;
    }

    private UserDataInfo initUserDataInfo(String mobilePhone, String linkman,LoginInfo user) {
        UserDataInfo userDataInfo = new UserDataInfo();
        userDataInfo.setLinkman(linkman);
        userDataInfo.setMobilePhone(mobilePhone);
        userDataInfo.setSourceFlag(0);
        userDataInfoRecordMapper.insert(userDataInfo);
        insertSysOperator(mobilePhone, userDataInfo.getId(),linkman, 1, null, null,
                SysStaticDataEnum.VERIFY_STS.UNAUTHORIZED, null,user);
        return userDataInfo;
    }

    /**
     * 如果新增员工时，员工不存于系统，则使用此方法往操作员表添加记录
     */
    public SysUser insertSysOperator(String loginAcct, Long userId, String operatorName, Integer lockFlag,
                                         String pwd, Long tenantId, Integer verifyStatus, String tenantCode,LoginInfo user){


        SysUser sysOperator = new SysUser();
        sysOperator.setTenantId(tenantId);
        sysOperator.setBillId(loginAcct);
        sysOperator.setCreateTime(LocalDateTime.now());
        sysOperator.setLoginAcct(loginAcct);
        sysOperator.setUserInfoId(userId);
        sysOperator.setName(operatorName);
        sysOperator.setOpUserId(user.getUserInfoId());
        sysOperator.setLockFlag(lockFlag);
        sysOperator.setState(verifyStatus);
        sysOperator.setTenantCode(tenantCode);

        iSysUserService.save(sysOperator);
        return sysOperator;
    }

    private SysUser getSysUser(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        BaseMapper<SysUser> baseMapper = iSysUserService.getBaseMapper();
        SysUser sysUser = baseMapper.selectById(loginInfo.getId());
        return sysUser;
    }

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

    @Override
    public void createTenantReceiverInfo(Long userId, Long tenantId,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        UserDataInfo userDataInfo = iUserDataInfoRecordService.getUserDataInfo(userId);
        if (null == userDataInfo) {
            throw new BusinessException("不存在的用户");
        }

        UserReceiverInfo userReceiverInfo = userReceiverInfoService.getUserReceiverInfoByUserId(userDataInfo.getId());
        if (null == userReceiverInfo) {
            userReceiverInfo = initUserReceiverInfo(userDataInfo.getLinkman(), userDataInfo.getId(),accessToken);
        }

        TenantReceiverRel rel = createTenantReceiverRel(userReceiverInfo, null, tenantId);
        sysOperLogService.saveSysOperLog(loginInfo,SysOperLogConst.BusiCode.UserReceivcer, rel.getId(),SysOperLogConst.OperType.Add,"新增收款人");
    }

    private TenantReceiverRel createTenantReceiverRel(UserReceiverInfo userReceiverInfo, String remark, Long tenantId) {
        if (null == tenantId || tenantId < 0) {
            throw new BusinessException("参数错误");
        }
        TenantReceiverRel tenantReceiverRel = baseMapper.getTenantReceiverRel(userReceiverInfo.getId(), tenantId);
        if (null != tenantReceiverRel) {
            return tenantReceiverRel;
        }

        com.youming.youche.order.domain.order.TenantReceiverRel tenantReceiverRels = new com.youming.youche.order.domain.order.TenantReceiverRel();
        BeanUtil.copyProperties(tenantReceiverRel,tenantReceiverRels);
        tenantReceiverRels.setReceiverId(userReceiverInfo.getId());
        tenantReceiverRels.setTenantId(tenantId);
        tenantReceiverRels.setCreateTime(LocalDateTime.now());
        tenantReceiverRels.setRemark(remark);
        tenantReceiverRels.setUpdateTime(LocalDateTime.now());

        tenantReceiverRelService.save(tenantReceiverRels);

        return tenantReceiverRel;
    }
}
