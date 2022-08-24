package com.youming.youche.system.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.system.api.ISysOrganizeService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserOrgRelService;
import com.youming.youche.system.domain.SysOrganize;
import com.youming.youche.system.domain.SysUserOrgRel;
import com.youming.youche.system.provider.mapper.SysOrganizeMapper;
import com.youming.youche.system.provider.utis.OrganizeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 组织表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-01-05
 */
@DubboService(version = "1.0.0")
public class SysOrganizeServiceImpl extends BaseServiceImpl<SysOrganizeMapper, SysOrganize>
        implements ISysOrganizeService {

    @Resource
    LoginUtils loginUtils;

    @Resource
    ISysUserOrgRelService sysUserOrgRelService;

    @Resource
    ISysTenantDefService sysTenantDefService;

    private static final Logger LOGGER = LoggerFactory.getLogger(SysOrganizeServiceImpl.class);

    @Override
    public List<SysOrganize> querySysOrganizeTree(String accessToken, Integer state, boolean hiddenNode) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<SysOrganize> sysOrganizes = getSysOrganizes(state, hiddenNode, loginInfo.getTenantId());

        Map<Long, SysOrganize> sysOrganizeMap = Maps.newHashMap();
        List<SysOrganize> resultList = Lists.newArrayList();
        for (SysOrganize sysOrganize : sysOrganizes) {
            sysOrganizeMap.put(sysOrganize.getId(), sysOrganize);
        }
        for (SysOrganize sysOrganize : sysOrganizes) {
            SysOrganize child = sysOrganize;
            if (child.getParentOrgId() == -1L) {
                resultList.add(sysOrganize);
            } else {
                SysOrganize parent = sysOrganizeMap.get(child.getParentOrgId());
                parent.getChildren().add(sysOrganize);
            }
        }

        return resultList;
    }

    @Override
    public List<SysOrganize> querySysOrganizeTreeTrailer(String accessToken, Integer state, boolean hiddenNode) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<SysOrganize> sysOrganizes = getSysOrganizes(state, hiddenNode, loginInfo.getTenantId());

        Map<Long, SysOrganize> sysOrganizeMap = Maps.newHashMap();
        List<SysOrganize> resultList = Lists.newArrayList();
        for (SysOrganize sysOrganize : sysOrganizes) {
            sysOrganizeMap.put(sysOrganize.getId(), sysOrganize);
        }
        for (SysOrganize sysOrganize : sysOrganizes) {
            resultList.add(sysOrganize);
        }

        return resultList;
    }

    /**
     * 方法实现说明
     *
     * @param state      组织的状态（0：废弃， 1：可用， null：全部）
     * @param hiddenNode 是否包含隐藏的根节点(如果不包含隐藏根节点无法构建组织树)
     * @param tenantId
     * @return java.util.List<com.youming.youche.system.domain.SysOrganize>
     * @throws
     * @author terry
     * @date 2022/1/8 15:32
     */
    private List<SysOrganize> getSysOrganizes(Integer state, boolean hiddenNode, Long tenantId) {

        QueryWrapper<SysOrganize> wrapper = new QueryWrapper<>();
        if (!hiddenNode) {
            wrapper.eq("parent_org_id", -1L);
        }
        wrapper.eq("tenant_id", tenantId);
        if (null != state) {
            wrapper.eq("state", state);
        }
        return baseMapper.selectList(wrapper);
    }


    @Override
    public IPage<SysOrganize> querySysOrganize(String accessToken, Integer state, Integer pageNum, Integer pageSize, String orgName) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        LambdaQueryWrapper<SysOrganize> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysOrganize::getTenantId, loginInfo.getTenantId());
        if (null != state) {
            wrapper.eq(SysOrganize::getState, state);
        }
        if (StringUtils.isNotBlank(orgName)) {
            wrapper.like(SysOrganize::getOrgName, orgName);
        }
        Page<SysOrganize> sysOrganizePage = new Page<>(pageNum, pageSize);
        IPage<SysOrganize> sysOrganizeIPage = baseMapper.selectPage(sysOrganizePage, wrapper);
        for (SysOrganize record : sysOrganizeIPage.getRecords()) {
            if (record.getParentOrgId() == -1) {
                sysOrganizeIPage.getRecords().remove(record);
                break;
            }
        }
        return sysOrganizeIPage;
    }

    @Override
    public List<SysOrganize> querySysOrganize(String accessToken, Integer state) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        return querySysOrganize(loginInfo.getTenantId(), state);
    }

    @Override
    public List<SysOrganize> querySysOrganize(Long tenantId, Integer state) {
        QueryWrapper<SysOrganize> wrapper = new QueryWrapper<>();
        wrapper.eq("tenant_id", tenantId);
        if (null != state) {
            wrapper.eq("state", state);
        }
        List<SysOrganize> sysOrganizes = baseMapper.selectList(wrapper);
        return sysOrganizes;
    }

    @Override
    public List<Long> getSubOrgList(Long tenantId, List<Long> orgIds) {
        List<SysOrganize> sysOrganizeList = querySysOrganize(tenantId, 1);
        List<Long> childrenOrgIdList = new ArrayList<>();
        if (CollectionUtils.isEmpty(sysOrganizeList)) {
            return Lists.newArrayList();
        }
        for (Long orgId : orgIds
        ) {
            List<Long> childrenOrgIdListNew = OrganizeUtil.getAllChildOrgId(sysOrganizeList, orgId);
            childrenOrgIdList.addAll(childrenOrgIdListNew);
            childrenOrgIdList.add(orgId);
        }
        LinkedHashSet<Long> hashSet = new LinkedHashSet<>(childrenOrgIdList);
        ArrayList<Long> listWithoutDuplicates = new ArrayList<>(hashSet);
        LOGGER.info(listWithoutDuplicates.toString());
        return listWithoutDuplicates;
    }

    @Override
    public List<Long> getSubOrgList(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        return getSubOrgList(loginInfo.getTenantId(), loginInfo.getOrgIds());
    }

    @Override
    public List<SysOrganize> selectAllByAccessToken(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<SysOrganize> sysOrganizeList = baseMapper.selectAllByUserInfoId(loginInfo.getUserInfoId(), loginInfo.getTenantId());
        return sysOrganizeList;
    }

    @Override
    public List<SysOrganize> querySysOrganizeList(Long tid, Long parentOrgId, Integer status) {
        QueryWrapper<SysOrganize> sysOrganizeQueryWrapper = new QueryWrapper<>();
        sysOrganizeQueryWrapper.eq("tenant_id", tid);
        if (parentOrgId != null) {
            sysOrganizeQueryWrapper.eq("parent_org_id", parentOrgId);
        }
        if (status != null) {
            sysOrganizeQueryWrapper.eq("state", status);
        }

        return baseMapper.selectList(sysOrganizeQueryWrapper);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class})
    public boolean create(SysOrganize sysOrganize, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (isExist(null, sysOrganize.getOrgName(), loginInfo.getTenantId())) {
            throw new BusinessException("已存在相同名称的部门");
        }
        baseMapper.insert(sysOrganize);
        SysUserOrgRel sysUserOrgRel = new SysUserOrgRel();
        sysUserOrgRel.setUserInfoId(sysOrganize.getUserInfoId()).setOrgId(sysOrganize.getId()).setState(1)
                .setOpId(loginInfo.getId()).setTenantId(sysOrganize.getTenantId());
        boolean b = sysUserOrgRelService.create(sysUserOrgRel);
        return b;
    }

    @Override
    public boolean update(SysOrganize sysOrganize, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        SysOrganize sysOrganizeOld = get(sysOrganize.getId());
        if (null == sysOrganizeOld) {
            throw new BusinessException("不存在的部门");
        }
        if (isExist(sysOrganize.getId(), sysOrganize.getOrgName(), loginInfo.getTenantId())) {
            throw new BusinessException("已存在相同名称的部门");
        }
        // 不允许修改三个组织（隐藏根、根、废弃）
        // 查询隐藏跟
        LambdaQueryWrapper<SysOrganize> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysOrganize::getParentOrgId, -1L).eq(SysOrganize::getTenantId, loginInfo.getTenantId());
        SysOrganize hiddenRoot = baseMapper.selectOne(wrapper);
        if (sysOrganize.getId().equals(hiddenRoot.getId())) {
            throw new BusinessException("不允许修改此部门");
        }
        // 查询根
        wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysOrganize::getParentOrgId, hiddenRoot.getId()).eq(SysOrganize::getTenantId,
                loginInfo.getTenantId());
        List<SysOrganize> sysOrganizes = baseMapper.selectList(wrapper);
        for (SysOrganize organize : sysOrganizes) {
            if (sysOrganize.getId().equals(organize.getId())) {
                throw new BusinessException("不允许修改此部门");
            }
        }
        // 查询所有组织
        List<SysOrganize> sysOrganizeList = getSysOrganizes(null, true, loginInfo.getTenantId());
        // 待验证
        if (!checkUpdateParentId(sysOrganizeList, sysOrganize.getId(), sysOrganize.getParentOrgId())) {
            throw new BusinessException("请选择正确的上级部门");
        }

        sysOrganizeOld.setOrgName(sysOrganize.getOrgName());
        sysOrganizeOld.setParentOrgId(sysOrganize.getParentOrgId());
        sysOrganizeOld.setLinkMan(sysOrganize.getLinkMan());
        sysOrganizeOld.setUserInfoId(sysOrganize.getUserInfoId());
        baseMapper.updateById(sysOrganizeOld);

        List<Long> userIdList = new ArrayList<>(1);
        List<Long> orgIdList = new ArrayList<>(1);
        userIdList.add(sysOrganizeOld.getUserInfoId());
        orgIdList.add(sysOrganizeOld.getId());
        SysUserOrgRel sysUserOrgRel = sysUserOrgRelService.selectByOrgIdAndUserId(sysOrganize.getId(),
                sysOrganize.getUserInfoId());
        if (null == sysUserOrgRel) {
            sysUserOrgRelService.saveUserOragnizeRel(orgIdList, userIdList, loginInfo.getTenantId(), loginInfo.getId());
        }

        return true;
    }

    /**
     * 方法实现说明 判断把后代组织设置为自己的上级
     *
     * @param sysOragnizeList
     * @param orgId
     * @param parentId
     * @return boolean
     * @throws
     * @author terry
     * @date 2022/1/8 15:45
     */
    private boolean checkUpdateParentId(List<SysOrganize> sysOragnizeList, Long orgId, Long parentId) {

        if (orgId.equals(parentId)) { // 不允许把上级设为自己
            return false;
        }

        if (CollectionUtils.isEmpty(sysOragnizeList)) {
            return false;
        }
        List<Long> childOrgIdList = OrganizeUtil.getAllChildOrgId(sysOragnizeList, orgId);
        // 不允许把上级设为自己的下级
        for (Long id : childOrgIdList) {
            if (id.equals(parentId)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean remove(Long orgId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        SysOrganize sysOrganizeOld = get(orgId);
        if (null == sysOrganizeOld) {
            throw new BusinessException("不存在的部门");
        }
        if (sysOrganizeOld.getState() == SysStaticDataEnum.SYS_STATE_DESC.STATE_NO) {
            throw new BusinessException("该部门已是废弃部门,无需删除!");
        }

        // 不允许修改三个组织（隐藏根、根、废弃）
        // 查询隐藏跟
        LambdaQueryWrapper<SysOrganize> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysOrganize::getParentOrgId, -1L).eq(SysOrganize::getTenantId, loginInfo.getTenantId());
        SysOrganize hiddenRoot = baseMapper.selectOne(wrapper);
        if (orgId.equals(hiddenRoot.getId())) {
            throw new BusinessException("不允许删除此部门");
        }
        // 查询根
        wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysOrganize::getParentOrgId, hiddenRoot.getId()).eq(SysOrganize::getTenantId,
                loginInfo.getTenantId());
        List<SysOrganize> sysOrganizes = baseMapper.selectList(wrapper);
        for (SysOrganize organize : sysOrganizes) {
            if (orgId.equals(organize.getId())) {
                throw new BusinessException("不允许删除根部门");
            }
        }

        // 获取该组织下的所有子组织
        List<SysOrganize> sysOrganizeList = getSysOrganizes(null, true, loginInfo.getTenantId());
        List<Long> childOrgIdList = OrganizeUtil.getAllChildOrgId(sysOrganizeList, orgId);
        childOrgIdList.add(orgId);
        // 检查该组织及其所有自组织已经没有员工
        List<SysUserOrgRel> sysUserOrgRels = sysUserOrgRelService.selectByTenantIdAndOrgIds(loginInfo.getTenantId(),
                childOrgIdList);
        if (!CollectionUtils.isEmpty(sysUserOrgRels)) {
            throw new BusinessException("请移除部门下的所有员工");
        }
        // 判断是否售前售后组织
        boolean b = sysTenantDefService.selectByPreSaleOrgIdOrAfterSaleOrgId(childOrgIdList);
        if (b) {
            throw new BusinessException("请移除车队中的责任部门");
        }
        boolean remove = remove(orgId);
        return remove;
    }


    /**
     * 当前租户下，根据orgId获取组织(不包含子节点数据)
     */
    public SysOrganize getCurrentTenantSysOragnizeById(Long tenantId, Long orgId) {
        List<SysOrganize> oragnizeList = querySysOrganize(tenantId, null);
        return OrganizeUtil.getSysOrganizeById(oragnizeList, orgId);
    }


    /**
     * 当前租户下，根据orgId获取组织名称
     */
    @Override
    public String getCurrentTenantOrgNameById(Long tenantId, Long orgId) {
        SysOrganize sysOragnize = getCurrentTenantSysOragnizeById(tenantId, orgId);
        if (null != sysOragnize) {
            return sysOragnize.getOrgName();
        }
        return null;
    }


    /**
     * 根据组织ID获取数据组织名称
     *
     * @param orgId 网点id
     * @return
     */
    @Override
    public String getOrgNameByOrgId(Long orgId, Long tenantId) {
        if (orgId == null) {
            return "";
        }
        return getCurrentTenantOrgNameById(tenantId, orgId);
    }

    @Override
    public List<SysOrganize> selectByUserInfoIdAndTenantId(Long userInfoId, Long tenantId) {
        List<Long> orgIds = sysUserOrgRelService.selectIdByUserInfoIdAndTenantId(userInfoId, tenantId);
        LambdaQueryWrapper<SysOrganize> wrapper = Wrappers.lambdaQuery();
        wrapper.in(SysOrganize::getId, orgIds);
        return list(wrapper);
    }

    @Override
    public List<SysOrganize> getSysOrganizeBytenantId(Long tenantId) {
        LambdaQueryWrapper<SysOrganize> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysOrganize::getTenantId, tenantId);
        return this.list(wrapper);
    }

    @Override
    public SysOrganize getRootOragnize(Long tenantId) {

        SysOrganize sysOragnize = this.getHiddenRootOragnize(tenantId);

        LambdaQueryWrapper<SysOrganize> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysOrganize::getParentOrgId, sysOragnize.getId());
        queryWrapper.eq(SysOrganize::getTenantId, tenantId);
        queryWrapper.eq(SysOrganize::getState, SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        queryWrapper.orderByAsc(SysOrganize::getId);
        List<SysOrganize> list = this.list();
        if (list != null && list.size() > 1) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public String queryOrgIdByName(String name, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        LambdaQueryWrapper<SysOrganize> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysOrganize::getTenantId, loginInfo.getTenantId());
        queryWrapper.like(SysOrganize::getOrgName, "%" + name + "%");
        List<SysOrganize> list = this.list(queryWrapper);

        String idStr = "";
        StringBuffer sb = new StringBuffer();
        for (SysOrganize sysOrganize : list) {
            sb.append("'").append(sysOrganize.getId()).append("',");
        }

        if (sb.length() != 0) {
            idStr = sb.substring(0, sb.length() - 1);
        }

        return idStr;
    }

    @Override
    public List<SysOrganize> getSysOragnizeList(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        LambdaQueryWrapper<SysOrganize> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysOrganize::getTenantId, loginInfo.getTenantId());
        List<SysOrganize> list = this.list(queryWrapper);

        List<SysOrganize> result = new ArrayList<SysOrganize>();

        if (null != list && !list.isEmpty()) {
            for (SysOrganize s : list) {
                if (null != s.getState() && s.getState().intValue() != 0 && s.getParentOrgId() != -1) {
                    result.add(s);
                }
            }
        }
        return result;
    }

    /**
     * 获取隐藏的根组织（parentOrgId = -1）
     */
    public SysOrganize getHiddenRootOragnize(Long tenantId) {
        LambdaQueryWrapper<SysOrganize> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysOrganize::getParentOrgId, -1L);
        queryWrapper.eq(SysOrganize::getTenantId, tenantId);
        queryWrapper.orderByAsc(SysOrganize::getId);
        List<SysOrganize> list = this.list();
        if (list != null && list.size() > 1) {
            return list.get(0);
        }
        return null;
    }

    public boolean isExist(Long organizeId, String oragnizeName, Long tenantId) {
        LambdaQueryWrapper<SysOrganize> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysOrganize::getTenantId, tenantId);
        lambdaQueryWrapper.eq(SysOrganize::getOrgName, oragnizeName);
        lambdaQueryWrapper.eq(SysOrganize::getState, SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        if (null != organizeId && organizeId > 0L) {
            lambdaQueryWrapper.ne(SysOrganize::getId, organizeId);
        }
        List<SysOrganize> organize = this.list(lambdaQueryWrapper);
        if (null != organize && organize.size() > 0 && organize.get(0).getOrgName() != null) {
            return true;
        }
        return false;
    }

}
