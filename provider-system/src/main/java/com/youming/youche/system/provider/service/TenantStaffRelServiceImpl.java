package com.youming.youche.system.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseCode;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.record.api.account.IAccountBankRelService;
import com.youming.youche.record.common.EnumConsts;
import com.youming.youche.system.api.ISysOrganizeService;
import com.youming.youche.system.api.ISysRoleService;
import com.youming.youche.system.api.ISysStaticDataService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ITenantStaffRelService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.SysOrganize;
import com.youming.youche.system.domain.SysRole;
import com.youming.youche.system.domain.TenantStaffRel;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.dto.TenantStaffDto;
import com.youming.youche.system.dto.UserDataInfoAndStaffs;
import com.youming.youche.system.provider.mapper.TenantStaffRelMapper;
import com.youming.youche.system.utils.CommonUtils;
import com.youming.youche.system.vo.SysTenantVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 员工信息 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-01-04
 */
@DubboService(version = "1.0.0")
@Service
public class TenantStaffRelServiceImpl extends BaseServiceImpl<TenantStaffRelMapper, TenantStaffRel>
        implements ITenantStaffRelService {

    @Resource
    LoginUtils loginUtils;

    @Resource
    ISysOrganizeService sysOrganizeService;

    @Resource
    ISysRoleService sysRoleService;

    @Resource
    IUserDataInfoService userDataInfoService;

    @Resource
    ISysTenantDefService sysTenantDefService;

    @Resource
    ISysStaticDataService isysStaticDataService;

    @DubboReference(version = "1.0.0")
    IAccountBankRelService iAccountBankRelService;

    @Override
    public List<TenantStaffRel> getTenantStaff(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        return getTenantStaffByUserInfoId(loginInfo.getUserInfoId());
    }

    @Override
    public List<TenantStaffRel> getTenantStaffByUserInfoId(Long userInfoId) {
        List<TenantStaffRel> tenantStaffRelByUserId = baseMapper.getTenantStaffRelByUserId(userInfoId);
        return tenantStaffRelByUserId;
    }

    @Override
    public TenantStaffRel getTenantStaffByUserInfoIdAndTenantId(Long userInfoId, Long tenantId) {
        LambdaQueryWrapper<TenantStaffRel> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(TenantStaffRel::getTenantId, tenantId).eq(TenantStaffRel::getUserInfoId, userInfoId);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public boolean delByUserInfoIdAndTenantId(Long userInfoId, Long tenantId) {
        LambdaQueryWrapper<TenantStaffRel> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(TenantStaffRel::getTenantId, tenantId).eq(TenantStaffRel::getUserInfoId, userInfoId)
                .eq(TenantStaffRel::getState, SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        List<TenantStaffRel> tenantStaffRels = baseMapper.selectList(wrapper);
        for (TenantStaffRel tenantStaffRel : tenantStaffRels) {
            tenantStaffRel.setState(0);
        }
        boolean b = saveOrUpdateBatch(tenantStaffRels);
        return b;
    }

    @Override
    public TenantStaffRel getTenantStaffByUserInfoIdAndTenantIdAndState(Long userInfoId, Long tenantId, int state) {
        LambdaQueryWrapper<TenantStaffRel> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(TenantStaffRel::getTenantId, tenantId).eq(TenantStaffRel::getUserInfoId, userInfoId)
                .eq(TenantStaffRel::getState, state);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public IPage<TenantStaffDto> get(String accessToken, Integer pageNum, Integer pageSize, String phone, String linkman,
                                     String number, String position, Integer lockFlag, String orgId) {

        LoginInfo loginInfo = loginUtils.get(accessToken);
        SysTenantVo sysTenantVo = sysTenantDefService.getTenantById(loginInfo.getTenantId());
        Page<TenantStaffRel> userDataInfoPage = new Page<>(pageNum, pageSize);
        IPage<TenantStaffDto> tenantStaffDtos = baseMapper.selectAll(userDataInfoPage, phone, linkman, number, position,
                loginInfo.getTenantId(), lockFlag, orgId);
        queryNameByRoleAndOrg(loginInfo, tenantStaffDtos.getRecords());
        tenantStaffDtos.getRecords().forEach(tenantStaffDto->{
            tenantStaffDto.setAdminUser(sysTenantVo.getAdminUser());
            //判断员工是否绑定银行卡
            if (iAccountBankRelService.isUserTypeBindCardAll(tenantStaffDto.getUserInfoId(), SysStaticDataEnum.USER_TYPE.CUSTOMER_USER)) {
                tenantStaffDto.setBankBindState(EnumConsts.BIND_STATE.BIND_STATE_ALL);
                tenantStaffDto.setBankBindStateName(isysStaticDataService.getSysStaticData("BIND_STATE", EnumConsts.BIND_STATE.BIND_STATE_ALL + "").getCodeName());
            } else {
                tenantStaffDto.setBankBindState(EnumConsts.BIND_STATE.BIND_STATE_NONE);
                tenantStaffDto.setBankBindStateName(isysStaticDataService.getSysStaticData("BIND_STATE", EnumConsts.BIND_STATE.BIND_STATE_NONE + "").getCodeName());
            }
        });
        return tenantStaffDtos;
    }

    private void queryNameByRoleAndOrg(LoginInfo loginInfo, List<TenantStaffDto> tenantStaffDtos) {
        List<SysOrganize> sysOrganizeList = sysOrganizeService.querySysOrganize(loginInfo.getTenantId(), null);

        Map<Long, String> map = Maps.newHashMap();
        for (SysOrganize sysOrganize : sysOrganizeList) {
            map.put(sysOrganize.getId(), sysOrganize.getOrgName());
        }

        for (TenantStaffDto tenantStaffDto : tenantStaffDtos) {
            List<Long> longs = CommonUtils.convertLongIdList(tenantStaffDto.getOrgIds());
            String name = "";
            for (Long aLong : longs) {
                if (StringUtils.isNotBlank(name)) {
                    name += ",";
                }
                name += map.get(aLong);
            }
            tenantStaffDto.setOrgNames(name);
        }
        List<SysRole> sysRoles = sysRoleService.getAll(loginInfo.getTenantId());

        Map<Long, String> roleMap = Maps.newHashMap();
        for (SysRole sysRole : sysRoles) {
            roleMap.put(sysRole.getId(), sysRole.getRoleName());
        }

        for (TenantStaffDto tenantStaffDto : tenantStaffDtos) {
            List<Long> longs = CommonUtils.convertLongIdList(tenantStaffDto.getRoleIds());
            String name = "";
            for (Long aLong : longs) {
                if (StringUtils.isNotBlank(name)) {
                    name += ",";
                }
                name += roleMap.get(aLong);
            }
            tenantStaffDto.setRoleNames(name);
        }
    }

    @Override
    public List<TenantStaffDto> get(String accessToken, Long userInfoId) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<TenantStaffDto> tenantStaffDtos = baseMapper.selectByUserInfoId(loginInfo.getTenantId(), userInfoId);
        queryNameByRoleAndOrg(loginInfo, tenantStaffDtos);
        return tenantStaffDtos;
    }

    @Override
    public Page<TenantStaffRel> queryStaffInfo(Page<TenantStaffRel> page, Integer lockFlag, String accessToken,
                                               Long userAccount,String staffName) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Page<TenantStaffRel> page1 = baseMapper.queryStaffInfo(page, lockFlag, loginInfo.getTenantId(),userAccount,staffName);
        return page1;
    }

    @Override
    public UserDataInfoAndStaffs selectByPhone(String phone) {
        UserDataInfo userDataInfo = userDataInfoService.getPhone(phone);
        if (userDataInfo==null){
            throw new BusinessException(ResponseCode.USER_NOT_EXIST);
        }
        LambdaQueryWrapper<TenantStaffRel> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(TenantStaffRel::getUserInfoId,userDataInfo.getId());
        List<TenantStaffRel> tenantStaffRels = baseMapper.selectList(wrapper);
        UserDataInfoAndStaffs userDataInfoAndStaffs = new UserDataInfoAndStaffs();
        userDataInfoAndStaffs.setUserDataInfo(userDataInfo);
        userDataInfoAndStaffs.setStaffRels(tenantStaffRels);

        return userDataInfoAndStaffs;

    }

    @Override
    public Page<TenantStaffRel> getStaffInfo(Page<TenantStaffRel> page, Integer lockFlag, String accessToken, Long userAccount, String staffName) {
        Page<TenantStaffRel> tenantStaffRelPage = baseMapper.queryStaffInfo(page, lockFlag, null, null, staffName);
        return tenantStaffRelPage;
    }

    @Override
    public Page<TenantStaffRel> getOrderStaffInfo(Page<TenantStaffRel> page, Integer lockFlag, String accessToken, Long userAccount, String staffName) {
        Page<TenantStaffRel> tenantStaffRelPage = baseMapper.queryOrderStaffInfo(page, lockFlag, null, null, staffName);
        return tenantStaffRelPage;
    }

    @Override
    public Page<TenantStaffRel> getOrderStaffInfoBytenantId(Page<TenantStaffRel> page, Integer lockFlag, String accessToken, Long userAccount, String staffName) {
        LoginInfo user = loginUtils.get(accessToken);
        Page<TenantStaffRel> tenantStaffRelPage = baseMapper.queryStaffInfo(page, lockFlag, user.getTenantId(), null, staffName);
        return tenantStaffRelPage;
    }

    @Override
    public Boolean isStaff(Long userId, Long tenantId) {
        LambdaQueryWrapper<TenantStaffRel> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(TenantStaffRel::getUserInfoId, userId);
        wrapper.eq(TenantStaffRel::getState, 1);
        if (null != tenantId) {
            wrapper.eq(TenantStaffRel::getTenantId, tenantId);
        }

        List<TenantStaffRel> list = baseMapper.selectList(wrapper);
        return CollectionUtils.isNotEmpty(list);
    }

}
