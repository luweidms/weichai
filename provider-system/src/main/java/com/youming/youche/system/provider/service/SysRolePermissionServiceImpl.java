package com.youming.youche.system.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.system.api.ISysRolePermissionService;
import com.youming.youche.system.domain.SysRolePermission;
import com.youming.youche.system.provider.mapper.SysRolePermissionMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 角色权限表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
@DubboService(version = "1.0.0")
@Service
public class SysRolePermissionServiceImpl extends BaseServiceImpl<SysRolePermissionMapper, SysRolePermission>
		implements ISysRolePermissionService {

	@Override
	public List<SysRolePermission> selectAllByRoleId(Long roleId) {
		LambdaQueryWrapper<SysRolePermission> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(SysRolePermission::getRoleId, roleId);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public boolean removeRole(Long roleId) {
		LambdaQueryWrapper<SysRolePermission> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(SysRolePermission::getRoleId, roleId);
		boolean remove = remove(wrapper);
		return remove;
	}

}
