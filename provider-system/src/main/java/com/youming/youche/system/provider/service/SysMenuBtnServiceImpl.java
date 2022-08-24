package com.youming.youche.system.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.system.api.ISysMenuBtnService;
import com.youming.youche.system.domain.SysMenuBtn;
import com.youming.youche.system.provider.mapper.SysMenuBtnMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;


/**
 * <p>
 * 系统菜单按钮表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-01-20
 */
@DubboService(version = "1.0.0")
@Service
public class SysMenuBtnServiceImpl extends BaseServiceImpl<SysMenuBtnMapper, SysMenuBtn> implements ISysMenuBtnService {

    @Resource
    LoginUtils loginUtils;


    @Override
    public List<SysMenuBtn> selectAll() {
        return baseMapper.selectList(null);
    }

    @Override
    public List<SysMenuBtn> selectByMenuId(Long id) {
        LambdaQueryWrapper<SysMenuBtn> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysMenuBtn::getMenuId,id);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<SysMenuBtn> selectByUserId(Long id, Long tenantId) {
        List<SysMenuBtn> sysMenuBtns = baseMapper.selectAllByUserIdAndTenandId(id,tenantId);
        sysMenuBtns.removeAll(Collections.singleton(null));
        return sysMenuBtns;
    }

    @Override
    public List<SysMenuBtn> selectAllByUserIdAndData(long userId) {
        return baseMapper.selectAllByUserIdAndData(userId);
    }

    @Override
    public List<SysMenuBtn> selectAllByUserIdAndData(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        return  selectAllByUserIdAndData(loginInfo.getUserInfoId());
    }

    @Override
    public List<SysMenuBtn> getRoleId(Long roleId) {
        List<SysMenuBtn> sysMenuBtns =baseMapper.selectAllByRoleId(roleId);
        return sysMenuBtns;

    }
}
