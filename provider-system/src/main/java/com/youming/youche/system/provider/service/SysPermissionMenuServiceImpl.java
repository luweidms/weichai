package com.youming.youche.system.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseCode;
import com.youming.youche.system.api.ISysPermissionMenuService;
import com.youming.youche.system.api.ISysPermissionService;
import com.youming.youche.system.constant.RoleConstant;
import com.youming.youche.system.domain.SysPermission;
import com.youming.youche.system.domain.SysPermissionMenu;
import com.youming.youche.system.domain.SysUserRole;
import com.youming.youche.system.provider.mapper.SysPermissionMenuMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
public class SysPermissionMenuServiceImpl extends BaseServiceImpl<SysPermissionMenuMapper, SysPermissionMenu>
		implements ISysPermissionMenuService {

	@Resource
	ISysPermissionService sysPermissionService;


	@Override
	@Transactional
	public boolean updateAll(List<Long> ids, Long role) {
		List<SysPermission> own = sysPermissionService.getRole(role, RoleConstant.PermissionType.MENU);
		if (CollectionUtils.isEmpty(own)){
			throw new BusinessException("未找到用户");
		}
		// 理论上一种权限对应一条记录
		Long permissionId = own.get(0).getId();

//		for (SysPermission sysPermission : own) {
//			if (sysPermission.getType().equals(type)) {
//				permissionId = sysPermission.getId();
//				break;
//			}
//		}

		QueryWrapper<SysPermissionMenu> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("permission_id", permissionId);
		baseMapper.delete(queryWrapper);
		List<SysPermissionMenu> list = Lists.newArrayList();
		for (Long id : ids) {
			list.add(new SysPermissionMenu().setMenuId(id).setPermissionId(permissionId));
		}
		Integer integer = baseMapper.insertBatchByMenuIds(permissionId, ids);
		if (integer != ids.size()) {
			throw new BusinessException(ResponseCode.MYSQL_TRANSIENT);
		}
		return true;
	}

	@Override
	public boolean creates(List<Long> ids, Long permissionId) {
		List<SysPermissionMenu> list = Lists.newArrayList();
		for (Long id : ids) {
			list.add(new SysPermissionMenu().setMenuId(id).setPermissionId(permissionId));
		}
		Integer integer = baseMapper.insertBatchByMenuIds(permissionId, ids);
		if (integer != ids.size()) {
			throw new BusinessException(ResponseCode.MYSQL_TRANSIENT);
		}
		return true;
	}

	@Override
	public boolean removePermissionId(Long permissionId) {
		LambdaQueryWrapper<SysPermissionMenu> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(SysPermissionMenu::getPermissionId, permissionId);

		List<SysPermissionMenu> sysPermissionMenus = baseMapper.selectList(wrapper);
		if (CollectionUtils.isNotEmpty(sysPermissionMenus)) {
			return remove(wrapper);
		}
		return true;
	}

}
