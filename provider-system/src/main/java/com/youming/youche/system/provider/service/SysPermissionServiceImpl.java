package com.youming.youche.system.provider.service;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.system.api.ISysPermissionBtnService;
import com.youming.youche.system.api.ISysPermissionMenuService;
import com.youming.youche.system.api.ISysPermissionService;
import com.youming.youche.system.api.ISysRoleService;
import com.youming.youche.system.domain.SysPermission;
import com.youming.youche.system.domain.SysRole;
import com.youming.youche.system.provider.mapper.SysPermissionMapper;
import com.youming.youche.system.vo.UpdatePermissionVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
@DubboService(version = "1.0.0")
@Service
public class SysPermissionServiceImpl extends BaseServiceImpl<SysPermissionMapper, SysPermission>
		implements ISysPermissionService {

	@Resource
	ISysPermissionMenuService sysPermissionMenuService;

	@Resource
	ISysPermissionBtnService sysPermissionBtnService;

	@Resource
	ISysRoleService sysRoleService;

	@Override
	public List<SysPermission> getRole(Long roleId) {
		return baseMapper.selectAllByRoleId(roleId);
	}

	@Override
	public List<SysPermission> getRole(Long roleId, Integer type) {
		return baseMapper.selectAllByRoleIdAndType(roleId,type);
	}

	@Override
	public boolean updateAll(UpdatePermissionVo permissionVo) {

		List<Long> buttons = permissionVo.getButtons();
		//更新按钮权限
		if (CollectionUtils.isNotEmpty(buttons)){
			boolean b = sysPermissionBtnService.updateAll(buttons, permissionVo.getRoleId());
		}
		List<Long> menus = permissionVo.getMenus();
		//更新菜单权限
		if (CollectionUtils.isNotEmpty(menus)){
			boolean b = sysPermissionMenuService.updateAll(menus, permissionVo.getRoleId());
		}
		SysRole sysRole = sysRoleService.get(permissionVo.getRoleId());
		if (StringUtils.isNotEmpty(permissionVo.getRoleName())){
			sysRole.setRoleName(permissionVo.getRoleName());
			sysRoleService.update(sysRole);
		}

		return true;
	}
}
