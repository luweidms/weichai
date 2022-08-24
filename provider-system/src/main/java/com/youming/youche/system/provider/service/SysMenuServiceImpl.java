package com.youming.youche.system.provider.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.system.api.ISysMenuBtnService;
import com.youming.youche.system.api.ISysMenuService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.domain.SysMenu;
import com.youming.youche.system.domain.SysMenuBtn;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.provider.mapper.SysMenuMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统菜单表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
@DubboService(version = "1.0.0")
public class SysMenuServiceImpl extends BaseServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {


    @Resource
    LoginUtils loginUtils;


    @Resource
    ISysMenuBtnService sysMenuBtnService;

    @Resource
    ISysTenantDefService sysTenantDefService;

    @Resource
    ISysUserService sysUserService;


    @Override
    public List<SysMenu> getAll(long userId, String accessToken) {
        // LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<>();
        // query.eq(SysUser::getId, userId);
//		userId = 644857;
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (loginInfo == null) {
            throw new BusinessException("请重新登陆");
        }
        SysTenantDef tenantDef = sysTenantDefService.get(loginInfo.getTenantId());

        SysUser sysUser = sysUserService.get(userId);
        if (tenantDef.getAdminUser().equals(sysUser.getUserInfoId())) {
            return getAll();
        } else {
            List<SysMenu> sysMenus = baseMapper.getAll(userId, loginInfo.getTenantId());
            sysMenus.removeAll(Collections.singleton(null));
            sysMenus = sysMenus.stream().distinct().collect(Collectors.toList());
            sysMenus = sysMenus.stream().sorted(Comparator.comparing(SysMenu::getId)).collect(Collectors.toList());

            List<SysMenuBtn> sysMenuBtns = sysMenuBtnService.selectByUserId(userId, loginInfo.getTenantId());
            sysMenuBtns = sysMenuBtns.stream().distinct().collect(Collectors.toList());
            sysMenuBtns = sysMenuBtns.stream().sorted(Comparator.comparing(SysMenuBtn::getId)).collect(Collectors.toList());

            if (sysMenuBtns.size() > 0) {
                bindMenuBtn(sysMenus, sysMenuBtns);

            }
            return getTreeSysMenus(sysMenus);
        }
    }

    @Override
    public List<SysMenu> getAll(LoginInfo loginInfo) {
        if (loginInfo.getTenantId() == -1L) {
            return getAll();
        }
        SysTenantDef tenantDef = sysTenantDefService.get(loginInfo.getTenantId());

        SysUser sysUser = sysUserService.get(loginInfo.getId());
        if (tenantDef.getAdminUser().equals(sysUser.getUserInfoId())) {
            return getAll();
        } else {
            List<SysMenu> sysMenus = baseMapper.getAll(loginInfo.getId(), loginInfo.getTenantId());
            sysMenus.removeAll(Collections.singleton(null));
            List<SysMenuBtn> sysMenuBtns = sysMenuBtnService.selectByUserId(loginInfo.getId(), loginInfo.getTenantId());
            if (sysMenuBtns.size() > 0) {
                bindMenuBtn(sysMenus, sysMenuBtns);
            }
            return getTreeSysMenus(sysMenus);
        }
    }

    @Override
    public List<SysMenu> getAll() {
        List<SysMenu> sysMenus = baseMapper.selectList(null);
        List<SysMenuBtn> sysMenuBtns = sysMenuBtnService.selectAll();
        bindMenuBtn(sysMenus, sysMenuBtns);

        return getTreeSysMenus(sysMenus);
    }

    /**
     * 方法实现说明  菜单与按钮绑定
     *
     * @param sysMenus
     * @param sysMenuBtns
     * @return void
     * @throws
     * @author terry
     * @date 2022/1/21 11:28
     */
    private void bindMenuBtn(List<SysMenu> sysMenus, List<SysMenuBtn> sysMenuBtns) {

        Map<Long, List<SysMenuBtn>> map = new HashMap<>();

        for (SysMenuBtn sysMenuBtn : sysMenuBtns) {
            if (map.containsKey(sysMenuBtn.getMenuId())) {
                map.get(sysMenuBtn.getMenuId()).add(sysMenuBtn);
            } else {
                map.put(sysMenuBtn.getMenuId(), new ArrayList<SysMenuBtn>() {{
                    add(sysMenuBtn);
                }});
            }
        }
        for (SysMenu sysMenu : sysMenus) {
            sysMenu.setButtons(map.get(sysMenu.getId()));
        }
    }


    @Override
    public List<SysMenu> getRoleId(Long roleId) {
        //获取角色所有菜单及按钮
        List<SysMenu> sysMenuList = baseMapper.selectAllByRoleId(roleId);
        List<SysMenuBtn> sysMenuBtnList = sysMenuBtnService.getRoleId(roleId);
        sysMenuList.removeAll(Collections.singleton(null));
        sysMenuBtnList.removeAll(Collections.singleton(null));


        //获取所有菜单及按钮
        List<SysMenu> sysMenuAll = baseMapper.selectList(null);
        List<SysMenuBtn> sysMenuBtnAll = sysMenuBtnService.selectAll();

        Map<Long, Boolean> sysMenuHashMap = Maps.newHashMap();
        Map<Long, Boolean> sysMenuBtnHashMap = Maps.newHashMap();
        for (SysMenu sysMenu : sysMenuList) {
            sysMenuHashMap.put(sysMenu.getId(), true);
        }
        for (SysMenuBtn sysMenuBtn : sysMenuBtnList) {
            sysMenuBtnHashMap.put(sysMenuBtn.getId(), true);
        }

        for (SysMenu sysMenu : sysMenuAll) {
            sysMenu.setChecked(sysMenuHashMap.get(sysMenu.getId()) != null);
        }
        for (SysMenuBtn sysMenuBtn : sysMenuBtnAll) {
            sysMenuBtn.setChecked(sysMenuBtnHashMap.get(sysMenuBtn.getId()) != null);
        }


        bindMenuBtn(sysMenuAll, sysMenuBtnAll);

        return getTreeSysMenus(sysMenuAll);
    }


    /**
     * 获取树状结构数据
     */
    private List<SysMenu> getTreeSysMenus(List<SysMenu> sysMenus) {
        Map<Long, SysMenu> sysMenuHashMap = Maps.newHashMap();
        List<SysMenu> sysMenuList = Lists.newArrayList();
        for (SysMenu sysMenu : sysMenus) {
            sysMenuHashMap.put(sysMenu.getId(), sysMenu);
        }
        for (SysMenu sysMenu : sysMenus) {
            SysMenu child = sysMenu;
            if (child.getParentId() == -1L) {
                sysMenuList.add(child);
            } else {
                SysMenu parent = sysMenuHashMap.get(child.getParentId());
                if (null == parent) {
                    continue;
                }
                parent.getChildren().add(child);
            }
        }
        return sysMenuList;
    }
}
