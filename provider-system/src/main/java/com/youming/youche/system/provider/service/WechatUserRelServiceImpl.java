package com.youming.youche.system.provider.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.domain.WechatUserRel;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseCode;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.record.api.account.IAccountBankRelService;
import com.youming.youche.record.api.tenant.ITenantUserRelService;
import com.youming.youche.record.api.tenant.IUserReceiverInfoService;
import com.youming.youche.record.domain.tenant.TenantUserRel;
import com.youming.youche.record.domain.user.UserReceiverInfo;
import com.youming.youche.system.api.*;
import com.youming.youche.system.domain.*;
import com.youming.youche.system.dto.UserDataInfoDto;
import com.youming.youche.system.dto.WechatUserInfoDto;
import com.youming.youche.system.provider.mapper.WechatUserRelMapper;
import com.youming.youche.system.provider.utis.SysStaticDataRedisUtils;
import com.youming.youche.system.vo.WechatUserVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author chenzhe
 * @since 2022-04-13
 */
@DubboService(version = "1.0.0")
public class WechatUserRelServiceImpl extends BaseServiceImpl<WechatUserRelMapper, WechatUserRel> implements IWechatUserRelService {

    @Resource
    IUserDataInfoService userDataInfoService;

    @Resource
    ISysUserService sysUserService;

    @Resource
    ITenantStaffRelService tenantStaffRelService;

    @Resource
    ISysMenuService sysMenuService;

    @DubboReference(version = "1.0.0")
    IUserReceiverInfoService userReceiverInfoService;

    @DubboReference(version = "1.0.0")
    ITenantUserRelService tenantUserRelService;

    @DubboReference(version = "1.0.0")
    IAccountBankRelService accountBankRelService;

    @Resource
    ISysTenantDefService iSysTenantDefService;

    @Resource
    ISysAttachService iSysAttachService;

    @Resource
    LoginUtils loginUtils;

    @Resource
    RedisUtil redisUtil;

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class})
    public void create(WechatUserVo wechatUserVo) {

        boolean del = delByOpenId(wechatUserVo.getOpenId());
        SysUser sysUser = sysUserService.getSysOperatorByUserIdOrPhone(null, wechatUserVo.getPhone());

        if (sysUser == null) {
            throw new BusinessException("查找用户操作信息失败，请先注册！");
        }
        if (sysUser.getLockFlag() != null && sysUser.getLockFlag() == 2) { //帐户锁定，进入黑名单
            throw new BusinessException("帐户被锁定，请联系管理员!");
        }
        //获取用户信息
        UserDataInfo userDataInfo = userDataInfoService.getUserDataInfo(sysUser.getUserInfoId());

        if (userDataInfo == null) {
            throw new BusinessException("查找用户表信息失败!");
        }

        WechatUserRel wechatUserRel = new WechatUserRel();
        BeanUtil.copyProperties(wechatUserVo, wechatUserRel);
        wechatUserRel.setLinkman(sysUser.getLinkMan());
        wechatUserRel.setUserId(sysUser.getUserInfoId());
        wechatUserRel.setOpId(sysUser.getId());
        wechatUserRel.setCreator(sysUser.getId());
        wechatUserRel.setState(EnumConsts.STATE.STATE_YES);

        boolean save = save(wechatUserRel);
        if (!save) {
            throw new BusinessException(ResponseCode.DATA_IS_WRONG);
        }
    }


    @Override
    public boolean delByOpenId(String openId) {
        LambdaQueryWrapper<WechatUserRel> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(WechatUserRel::getOpenId, openId);
        return remove(wrapper);
    }

    @Override
    public List<WechatUserRel> getWechatUserRelByAppCodeAndState(Integer appCode, Integer state) {
        LambdaQueryWrapper<WechatUserRel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WechatUserRel::getAppCode, appCode);
        queryWrapper.eq(WechatUserRel::getState, state);
        return this.list(queryWrapper);
    }

    @Override
    public WechatUserInfoDto queryInfo(Integer platformType, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        UserDataInfo userDataInfo = userDataInfoService.get(loginInfo.getUserInfoId());
        UserDataInfoDto userDataInfoDto = UserDataInfoDto.of();
        BeanUtil.copyProperties(userDataInfo, userDataInfoDto);
        userDataInfoDto.setAuthStateName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue(redisUtil,
                EnumConsts.SysStaticData.CUSTOMER_AUTH_STATE, userDataInfo.getAuthState() + "").getCodeName());


        SysUser sysUser = sysUserService.get(loginInfo.getId());
        sysUser.setPassword(null);
        WechatUserInfoDto wechatUserInfoDto = new WechatUserInfoDto();
        wechatUserInfoDto.setSysUser(sysUser);
        wechatUserInfoDto.setUserDataInfo(userDataInfoDto);


        if (platformType == 4) {
            //获取车队基本信息
            SysTenantDef sysTenantDef = iSysTenantDefService.getById(sysUser.getTenantId());

            if (StringUtils.isNotBlank(sysTenantDef.getLogo())) {
                SysAttach byId = iSysAttachService.getById(sysTenantDef.getLogo());
                if (byId != null) {
                    FastDFSHelper client = null;
                    try {
                        client = FastDFSHelper.getInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String url = "";
                    try {
                        url = client.getHttpURL(byId.getStorePath()).split("\\?")[0];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    userDataInfoDto.setUserPriceUrl(url);
                }

            }

        }

        // 2服务商
        if (platformType == 2) {

        }
        // 司机
        else if (platformType == 3) {
            //判断用户是否为已认证
            wechatUserInfoDto.setAuth(1);
            if (userDataInfo.getAuthState() == null || userDataInfo.getAuthState() == SysStaticDataEnum.AUTH_STATE.AUTH_STATE1
                    || userDataInfo.getAuthState() == SysStaticDataEnum.AUTH_STATE.AUTH_STATE3 || StringUtils.isNotEmpty(userDataInfo.getVerifReason())) {
                wechatUserInfoDto.setAuth(0);
            } else {//判断在所属租户下是否已认证
                if (userDataInfo.getTenantId() != null) {
                    TenantUserRel tenantUserRel = tenantUserRelService.getAllTenantUserRelByUserId(loginInfo.getUserInfoId(), userDataInfo.getTenantId());
                    if (tenantUserRel != null) {
                        if (tenantUserRel.getState() == null || tenantUserRel.getState() == SysStaticDataEnum.AUTH_STATE.AUTH_STATE1
                                || tenantUserRel.getState() == SysStaticDataEnum.AUTH_STATE.AUTH_STATE3 || StringUtils.isNotEmpty(tenantUserRel.getStateReason())) {
                            wechatUserInfoDto.setAuth(0);
                        }
                    }

                }
            }
            //判断是否已经绑卡,司机只要判断对私卡就可以
            wechatUserInfoDto.setBindCard(0);
            try {
                if (accountBankRelService.isUserTypeBindCardAll(loginInfo.getUserInfoId(), SysStaticDataEnum.USER_TYPE.DRIVER_USER)) {
                    wechatUserInfoDto.setBindCard(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //驻场 ？
        else {
            // TODO: 2022/4/26 未知作用 --未完成
            List<TenantStaffRel> tenantStaffRels = tenantStaffRelService.getTenantStaffByUserInfoId(loginInfo.getUserInfoId());
            UserReceiverInfo userReceiverInfo = userReceiverInfoService.getUserReceiverInfoByUserId(loginInfo.getUserInfoId());
            //   List<SysUserOrgRel> sysUserOrgRels = sysUserOrgRelService.getByUserDataIdAndTenantId(loginInfo.getUserInfoId(), loginInfo.getTenantId());

            List<TenantStaffRel> tenantStaffRelNewList = new ArrayList<>();
            //过滤租户
            if (null != tenantStaffRels && !tenantStaffRels.isEmpty()) {
                for (TenantStaffRel rel : tenantStaffRels) {
                    if (rel.getTenantId() != SysStaticDataEnum.PT_TENANT_ID) {
                        tenantStaffRelNewList.add(rel);
                    }
                }
            }

            if (null == userReceiverInfo && tenantStaffRelNewList.isEmpty()) {
                throw new BusinessException("该用户不是代收人、员工或租户已被停用！请联系客服！");
            }

            //属于单租户的用户而且不是代收人，可以直接登录，先获取到tenantId与orgId放入seesion里
            if (tenantStaffRelNewList.size() == 1 && null == userReceiverInfo) {
                TenantStaffRel tenantStaffRel = tenantStaffRelNewList.get(0);
                if (null != tenantStaffRel.getState()
                        && tenantStaffRel.getState().intValue() == SysStaticDataEnum.STAFF_STATE.DELETE) {
                    throw new BusinessException("用户已被删除！请联系客服！");
                }
                if (null != tenantStaffRel.getLockFlag()
                        && tenantStaffRel.getLockFlag().intValue() == SysStaticDataEnum.LOCK_FLAG.LOCK_NO) {
                    throw new BusinessException("用户已被锁定！请联系客服！");
                }


//                SysUserOrgRel sysUserOrgRel = sysUserOrgRelService.getStasffDefaultOragnizeByUserId(loginInfo.getUserInfoId(), loginInfo.getTenantId());

            } else if (tenantStaffRelNewList.isEmpty() && null != userReceiverInfo) {
                //如果是某个车队的超管（包括小车队），需要返回车队ID
//                sysTenantDefService.
            }

        }
        // 查询车队端操作权限
        if (null != loginInfo.getTenantId()) {
            List<SysMenu> all = sysMenuService.getAll(loginInfo);
            List<Long> list = Lists.newArrayList();
            all.forEach(sysMenu -> {
                list.add(sysMenu.getId());
                //获取子级菜单和按钮权限id
                menuRecursive(list, sysMenu);
            });
            wechatUserInfoDto.setPermissionList(list);
        }

        return wechatUserInfoDto;
    }


    @Override
    public Boolean unbind(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        LambdaQueryWrapper<WechatUserRel> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(WechatUserRel::getPhone, loginInfo.getBillId());
        return remove(wrapper);
    }

    @Override
    public Boolean unbindPhone(String phone) {
        LambdaQueryWrapper<WechatUserRel> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(WechatUserRel::getPhone, phone);
        return remove(wrapper);
    }
    //菜单递归
    private void menuRecursive(List<Long> ids, SysMenu sysMenu) {
        List<SysMenu> sysMenuChildList = sysMenu.getChildren();
        if (sysMenuChildList != null && sysMenuChildList.size() > 0) {
            sysMenuChildList.forEach(childMenu -> {
                ids.add(childMenu.getId());
                //获取按钮权限id
                List<SysMenuBtn> buttons = childMenu.getButtons();
                if (buttons != null && buttons.size() > 0) {
                    buttons.forEach(sysMenuBtn -> ids.add(sysMenuBtn.getId()));
                }
                menuRecursive(ids, childMenu);
            });
        }
    }

}
