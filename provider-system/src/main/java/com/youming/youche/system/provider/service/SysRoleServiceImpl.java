package com.youming.youche.system.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseCode;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.system.api.ISysPermissionBtnService;
import com.youming.youche.system.api.ISysPermissionMenuService;
import com.youming.youche.system.api.ISysPermissionService;
import com.youming.youche.system.api.ISysRolePermissionService;
import com.youming.youche.system.api.ISysRoleService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserRoleService;
import com.youming.youche.system.constant.RoleConstant;
import com.youming.youche.system.domain.SysPermission;
import com.youming.youche.system.domain.SysRole;
import com.youming.youche.system.domain.SysRolePermission;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.dto.SysRoleDto;
import com.youming.youche.system.provider.mapper.SysRoleMapper;
import com.youming.youche.system.utils.CommonUtils;
import com.youming.youche.system.vo.CreateRoleMenuVo;
import com.youming.youche.system.vo.SysTenantVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 系统角色表;一个角色可以关联多个操作员，一个操作员可以有多个角色。;一个角色可以关联多个权限，一个权限可以被多个角色拥有。 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
@DubboService(version = "1.0.0")
public class SysRoleServiceImpl extends BaseServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

	@Resource
	LoginUtils loginUtils;

	@Resource
	ISysPermissionService sysPermissionService;

	@Resource
	ISysRolePermissionService sysRolePermissionService;

	@Resource
	ISysPermissionMenuService sysPermissionMenuService;

	@Resource
	ISysPermissionBtnService sysPermissionBtnService;

	@Resource
	ISysUserRoleService sysUserRoleService;

	@Resource
	ISysTenantDefService sysTenantDefService;



	@Override
	public IPage<SysRoleDto> getAll(String accessToken, Integer pageNum, Integer pageSize, String roleName, String opName) {

		LoginInfo loginInfo = loginUtils.get(accessToken);
		Page<SysRole> rolePage = new Page<>(pageNum,pageSize);
		IPage<SysRoleDto> sysRoleDtoIPage = baseMapper.selectAllByRoleNameAndOpNameAndTenantId(rolePage, roleName, opName, loginInfo.getTenantId());


		return sysRoleDtoIPage;
	}

	@Override
	public List<SysRole> getAll(Long tenantId) {
		LambdaQueryWrapper<SysRole> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(SysRole::getTenantId, tenantId);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public List<SysRole> getOwn(String accessToken) {
		LoginInfo loginInfo = loginUtils.get(accessToken);
		return baseMapper.getOwn(loginInfo.getId(), loginInfo.getTenantId());
	}

	@Override
	public Boolean hasPermissionAndMenuId(String accessToken, Long menuId) {
		LoginInfo loginInfo = loginUtils.get(accessToken);
		int count = baseMapper.selectCountByUserIdAndTenantIdAndMenuId(loginInfo.getId(),loginInfo.getTenantId(),menuId);
		return count==1;
	}

	@Override
	public Boolean hasPermissionAndBtnId(String accessToken, Long btnId) {
		LoginInfo loginInfo = loginUtils.get(accessToken);
		return hasPermissionAndBtnId(loginInfo,btnId);
	}

	@Override
	public Boolean hasPermissionAndBtnId(LoginInfo loginInfo, Long btnId) {
		if (null == loginInfo.getTenantId()) {
			return false;
		}
		SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(loginInfo.getTenantId());
		if (sysTenantDef.getAdminUser().equals(loginInfo.getUserInfoId())){
			return true;
		}
		Integer integer = baseMapper.selectCountByUserIdAndTenantIdAndBtnId(loginInfo.getId(), loginInfo.getTenantId(), btnId);
		return integer==1;
	}

	@Override
	public Boolean hasAllData(LoginInfo loginInfo) {
		if (null == loginInfo.getTenantId()) {
			return false;
		}
		SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(loginInfo.getTenantId());
		if (sysTenantDef != null && sysTenantDef.getAdminUser().equals(loginInfo.getUserInfoId())){
			return true;
		}
		return hasPermissionAndBtnId(loginInfo,630L);
	}

	@Override
	public Boolean hasCostPermission(LoginInfo loginInfo) {
		return hasPermissionAndBtnId(loginInfo,634L);
	}

	@Override
	public Boolean hasIncomePermission(LoginInfo loginInfo) {
		return hasPermissionAndBtnId(loginInfo,633L);
	}


	@Override
	public Boolean hasOrderCostPermission(LoginInfo user) {
		return hasPermissionAndBtnId(user,632L);
	}

	@Override
	public Boolean hasorderIncomePermission(LoginInfo user) {
		return  hasPermissionAndBtnId(user,631L);
	}

	@Override
	public Boolean hasOrderIncomePermission(LoginInfo user) {
		return hasPermissionAndBtnId(user,631L);
	}


	@Override
	@Transactional(rollbackFor = { RuntimeException.class, Error.class })
	public Long create(CreateRoleMenuVo createRoleMenuVo, String accessToken) {
		LoginInfo loginInfo = loginUtils.get(accessToken);
		LambdaQueryWrapper<SysRole> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(SysRole::getTenantId, loginInfo.getTenantId()).eq(SysRole::getRoleName,
				createRoleMenuVo.getRoleName());
		List<SysRole> sysRoles = baseMapper.selectList(wrapper);
		if (CollectionUtils.isNotEmpty(sysRoles)) {
			throw new BusinessException("已存在相同名称的角色");
		}
		SysRole sysRole = new SysRole();
		sysRole.setRoleName(createRoleMenuVo.getRoleName());

		sysRole.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
		sysRole.setRoleType(2);
		sysRole.setTenantId(loginInfo.getTenantId());
		sysRole.setOpUserId(loginInfo.getId());
		save(sysRole);

		// 建立角色和权限表类型关联（菜单和按钮，文件等）
		// 建立权限表
		SysPermission sysPermission = new SysPermission();
		sysPermission.setName(createRoleMenuVo.getRoleName());
		sysPermission.setType(RoleConstant.PermissionType.MENU);
		boolean save = sysPermissionService.save(sysPermission);
		if (!save) {
			throw new BusinessException(ResponseCode.MYSQL_TRANSIENT);
		}
		// 权限表与角色表关联
		SysRolePermission sysRolePermission = new SysRolePermission();
		sysRolePermission.setRoleId(sysRole.getId());
		sysRolePermission.setPermissionId(sysPermission.getId());
		boolean save1 = sysRolePermissionService.save(sysRolePermission);
		if (!save1) {
			throw new BusinessException(ResponseCode.MYSQL_TRANSIENT);
		}
		// 建立权限表和菜单表联系
//		List<Long> MenuIds = CommonUtils.convertLongIdList(createRoleMenuVo.getMenuIds());
		boolean creates = sysPermissionMenuService.creates(createRoleMenuVo.getMenus(), sysPermission.getId());
		if (!creates) {
			throw new BusinessException(ResponseCode.MYSQL_TRANSIENT);
		}
		// 建立权限表和按钮表联系
		//建立权限表
		SysPermission sysPermissionBtn = new SysPermission();
		sysPermissionBtn.setName(createRoleMenuVo.getRoleName());
		sysPermissionBtn.setType(RoleConstant.PermissionType.BUTTON);
		boolean saveBtn = sysPermissionService.save(sysPermissionBtn);
		if (!saveBtn) {
			throw new BusinessException(ResponseCode.MYSQL_TRANSIENT);
		}
		// 权限表与角色表关联
		SysRolePermission sysRolePermissionBtn = new SysRolePermission();
		sysRolePermissionBtn.setRoleId(sysRole.getId());
		sysRolePermissionBtn.setPermissionId(sysPermissionBtn.getId());
		boolean save2 = sysRolePermissionService.save(sysRolePermissionBtn);
		if (!save2) {
			throw new BusinessException(ResponseCode.MYSQL_TRANSIENT);
		}
		// 建立权限表和按钮表联系
		sysPermissionBtnService.creates(createRoleMenuVo.getButtons(),sysPermissionBtn.getId());

		// 建立权限表和文件等。。。
		return sysRole.getId();

	}

	@Override
	@Transactional(rollbackFor = { RuntimeException.class, Error.class })
	public boolean remove(Long roleId, String accessToken) {
		SysRole sysRole = get(roleId);
		if (null == sysRole) {
			throw new BusinessException("角色不存在");
		}
		if (sysRole.getRoleType().equals(1)) {
			throw new BusinessException("不允许删除超级管理员角色");
		}
		// 删除 角色表
		boolean remove = remove(roleId);
		if (!remove) {
			throw new BusinessException(ResponseCode.MYSQL_TRANSIENT);
		}

		// 删除角色用户表
		boolean b = sysUserRoleService.removeRole(roleId);
		if (!b) {
			throw new BusinessException(ResponseCode.MYSQL_TRANSIENT);
		}

		// 删除角色权限表

		List<SysPermission> sysPermissions = sysPermissionService.getRole(roleId);
		sysPermissions.removeAll(Collections.singleton(null));
		boolean b2 = sysRolePermissionService.removeRole(roleId);

		for (SysPermission sysPermission : sysPermissions) {
			// 删除权限菜单表
			if (RoleConstant.PermissionType.MENU == sysPermission.getType()) {
				boolean b1 = sysPermissionMenuService.removePermissionId(sysPermission.getId());
				if (!b1) {
					throw new BusinessException(ResponseCode.MYSQL_TRANSIENT);
				}
			}
			else if (RoleConstant.PermissionType.BUTTON == sysPermission.getType()) {

			}
			boolean remove1 = sysPermissionService.remove(sysPermission.getId());
			if (!remove1) {
				throw new BusinessException(ResponseCode.MYSQL_TRANSIENT);
			}
		}

		return true;
	}

}
