package com.youming.youche.system.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.system.api.ISysUserOrgRelService;
import com.youming.youche.system.domain.SysUserOrgRel;
import com.youming.youche.system.provider.mapper.SysUserOrgRelMapper;
import com.youming.youche.system.utils.CommonUtils;
import com.youming.youche.system.vo.CreateSysUserOrgVo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 用户组织关系表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-01-04
 */
@DubboService(version = "1.0.0")
@Service
public class SysUserOrgRelServiceImpl extends BaseServiceImpl<SysUserOrgRelMapper, SysUserOrgRel>
        implements ISysUserOrgRelService {

    @Resource
    LoginUtils loginUtils;

    @Override
    public List<SysUserOrgRel> getByUserDataIdAndTenantId(Long userInfoId, Long tenantId) {
        QueryWrapper<SysUserOrgRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_info_id", userInfoId).eq("tenant_id", tenantId);
        List<SysUserOrgRel> sysUserOrgRel = baseMapper.selectList(queryWrapper);
        return sysUserOrgRel;
    }

    @Override
    public List<Long> selectIdByUserInfoIdAndTenantId(Long userInfoId, Long tenantId) {
        LambdaQueryWrapper<SysUserOrgRel> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysUserOrgRel::getUserInfoId, userInfoId).eq(SysUserOrgRel::getTenantId, tenantId)
                .eq(SysUserOrgRel::getState, SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        List<SysUserOrgRel> sysOrganizeList = list(wrapper);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(sysOrganizeList)) {
            List<Long> orgIdList = new ArrayList<>(sysOrganizeList.size());
            for (SysUserOrgRel sysUserOrgRel : sysOrganizeList) {
                orgIdList.add(sysUserOrgRel.getOrgId());
            }
            return orgIdList;
        }
        return null;
    }

    @Override
    public List<SysUserOrgRel> getByAccessTokenAndTenantId(String accessToken, Long tenantId) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        QueryWrapper<SysUserOrgRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_info_id", loginInfo.getUserInfoId()).eq("tenant_id", tenantId);
        List<SysUserOrgRel> sysUserOrgRel = baseMapper.selectList(queryWrapper);
        return sysUserOrgRel;
    }

    @Override
    public int saveUserOragnizeRel(List<Long> orgIdList, List<Long> userIdList, Long tenantId, Long opId) {
        int count = 0;
        if (CollectionUtils.isEmpty(orgIdList) || CollectionUtils.isEmpty(userIdList)) {
            return 0;
        }
        for (Long userInfoId : userIdList) {
            LambdaQueryWrapper<SysUserOrgRel> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(SysUserOrgRel::getUserInfoId, userInfoId);
            List<SysUserOrgRel> sysUserOrgRels = baseMapper.selectList(wrapper);

            for (Long orgId : orgIdList) {

                boolean isExist = false;
                for (SysUserOrgRel sysUserOrgRel : sysUserOrgRels) {
                    if (sysUserOrgRel.getOrgId().equals(orgId)) {
                        isExist = true;
                        break;
                    }
                }
                if (isExist) {
                    continue;
                }
                SysUserOrgRel sysUserOrgRel = new SysUserOrgRel();
                sysUserOrgRel.setOrgId(orgId);
                sysUserOrgRel.setUserInfoId(userInfoId);
                sysUserOrgRel.setTenantId(tenantId);
                sysUserOrgRel.setOpId(opId);
                sysUserOrgRel.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
                // sysUserOrgRel.setOpDate(operDate);
                count += baseMapper.insert(sysUserOrgRel);

            }
        }

        return count;
    }

    @Override
    public int updateUserOragnizeRel(List<Long> orgId, List<Long> userInfoId, Long tenantId, Long opId) {
        delUserOragnizeRel(userInfoId, tenantId);
        int i = saveUserOragnizeRel(orgId, userInfoId, tenantId, opId);
        return i;
    }

    @Override
    public int delUserOragnizeRel(List<Long> userInfoId, Long tenantId) {
        return baseMapper.deleteByUserInfoIdAndTenantId(userInfoId, tenantId);
    }

    @Override
    public SysUserOrgRel selectByOrgIdAndUserId(Long orgId, Long userInfoId) {
        LambdaQueryWrapper<SysUserOrgRel> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysUserOrgRel::getOrgId, orgId).eq(SysUserOrgRel::getUserInfoId, userInfoId);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public List<SysUserOrgRel> selectByTenantIdAndOrgIds(Long tenantId, List<Long> orgIds) {
        return baseMapper.selectByTenantIdAndOrgIds(tenantId, orgIds);
    }

    @Override
    @Transactional
    public boolean remove(CreateSysUserOrgVo sysUserOrgVo, String accessToken) {
        remove(sysUserOrgVo.getOrgId(), sysUserOrgVo.getUserInfoIds(), accessToken);
        return true;
    }

    @Override
    public boolean remove(Long orgId, String userInfoIds, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<Long> ids = CommonUtils.convertLongIdList(userInfoIds);
        int i = baseMapper.deleteByOrgIdAndUserInfoIdAndTenantId(orgId, ids, loginInfo.getTenantId());
        return true;
    }

    @Override
    public List<String> getOrgNameByUserId(Long userId) {
        List<String> orgNameByUserId = baseMapper.getOrgNameByUserId(userId);
        return orgNameByUserId;
    }

    @Override
    public SysUserOrgRel getStasffDefaultOragnizeByUserId(Long userId, Long tenantId) {
        LambdaQueryWrapper<SysUserOrgRel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserOrgRel::getTenantId, tenantId);
        queryWrapper.eq(SysUserOrgRel::getUserInfoId, userId);
        queryWrapper.eq(SysUserOrgRel::getState, SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        List<SysUserOrgRel> userOrgRelList = this.list();
        if (!CollectionUtils.isEmpty(userOrgRelList)) {
            return userOrgRelList.get(0);
        }
        return null;
    }

    @Override
    @Transactional
    public boolean create(CreateSysUserOrgVo sysUserOrgVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<Long> userInfoIds = CommonUtils.convertLongIdList(sysUserOrgVo.getUserInfoIds());

        List<Long> orgIds = new ArrayList<>(1);
        orgIds.add(sysUserOrgVo.getOrgId());
        int i = saveUserOragnizeRel(orgIds, userInfoIds, loginInfo.getTenantId(), loginInfo.getId());
        return true;
    }

}
