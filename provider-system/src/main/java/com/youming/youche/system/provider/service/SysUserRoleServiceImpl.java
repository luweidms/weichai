package com.youming.youche.system.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseCode;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysRoleService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserRoleService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.ITenantStaffRelService;
import com.youming.youche.system.domain.SysRole;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.SysUserRole;
import com.youming.youche.system.domain.TenantStaffRel;
import com.youming.youche.system.dto.OrganizeStaffDto;
import com.youming.youche.system.provider.mapper.SysUserRoleMapper;
import com.youming.youche.system.utils.CommonUtils;
import com.youming.youche.system.vo.CreateSysUserRoleVo;
import com.youming.youche.system.vo.DelSysUserRoleVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 系统角色操作员关系表;ROLE_ID : 对应sys_role中的role_id;op_id : 对应sys_user中的operator_id 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
@DubboService(version = "1.0.0")
@Slf4j
@Service
public class SysUserRoleServiceImpl extends BaseServiceImpl<SysUserRoleMapper, SysUserRole>
		implements ISysUserRoleService {

	@Resource
	ISysTenantDefService sysTenantDefService;

	@Resource
	ISysUserService sysUserService;

	@Resource
	ISysRoleService sysRoleService;

	@Resource
	ITenantStaffRelService tenantStaffRelService;

	@Resource
	ISysOperLogService sysOperLogService;

	@Resource
	LoginUtils loginUtils;

	@Override
	public boolean create(CreateSysUserRoleVo userRoleVo, String accessToken) {
		LoginInfo loginInfo = loginUtils.get(accessToken);
		SysTenantDef sysTenantDef = sysTenantDefService.get(loginInfo.getTenantId());
		if (sysTenantDef.getAdminUser().equals(userRoleVo.getUserInfoId())) {
			throw new BusinessException("不允许修改超级管理员的权限");
		}

		SysUser sysUser = sysUserService.getByUserInfoId(userRoleVo.getUserInfoId());
		if (null == sysUser) {
			throw new BusinessException(ResponseCode.USER_NOT_EXIST);
		}
		List<Long> roleIdList = CommonUtils.convertLongIdList(userRoleVo.getRoleIds());
		// 移除已有授权信息
		LambdaQueryWrapper<SysUserRole> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(SysUserRole::getUserId, sysUser.getId()).eq(SysUserRole::getTenantId, loginInfo.getTenantId());

//		List<SysRole> sysRoles = sysRoleService.listByIds(roleIdList);
//		for (SysRole sysRole : sysRoles) {
//			if (sysRole.getRoleType()==1){
//				throw new BusinessException("不允许授予超级管理员的权限");
//			}
//		}
		int delete = baseMapper.delete(wrapper);

		// 重新建立授权关系
		if (CollectionUtils.isNotEmpty(roleIdList)) {
			for (Long roleId : roleIdList) {
				SysUserRole sysUserRole = new SysUserRole();
				sysUserRole.setLastOpUserId(loginInfo.getId());
				sysUserRole.setRoleId(roleId);
				sysUserRole.setUserId(sysUser.getId());
				sysUserRole.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
				sysUserRole.setTenantId(loginInfo.getTenantId());
				super.save(sysUserRole);
			}
		}
		TenantStaffRel tenantStaffRel = tenantStaffRelService.getTenantStaffByUserInfoIdAndTenantId(userRoleVo.getUserInfoId(),loginInfo.getTenantId());
		sysOperLogService.save( SysOperLogConst.BusiCode.Staff,tenantStaffRel.getId(),SysOperLogConst.OperType.Update,"授权",loginInfo);
		return true;
	}

	@Override
	public IPage<OrganizeStaffDto> getRole(Long roleId, String linkman, String loginacct, String staffPosition,
			Integer pageNum, Integer pageSize, String accessToken) {
		LoginInfo loginInfo = loginUtils.get(accessToken);
		Page<SysUserRole> userDataInfoPage = new Page<>(pageNum, pageSize);
		IPage<OrganizeStaffDto> organizeStaffDtoIPage = baseMapper.selectAllByRoleIdAndTenantId(userDataInfoPage,
				roleId, loginInfo.getTenantId(), linkman, loginacct, staffPosition);
		return organizeStaffDtoIPage;
	}

	@Override
	public boolean remove(DelSysUserRoleVo delSysUserRoleVo, String accessToken) {
		LoginInfo loginInfo = loginUtils.get(accessToken);
		SysRole sysRole = sysRoleService.get(delSysUserRoleVo.getRoleId());
		if (sysRole.getRoleType()==1){
			throw new BusinessException("超管权限不允许修改");
		}
		List<Long> userIds = CommonUtils.convertLongIdList(delSysUserRoleVo.getUserIds());
		int i = baseMapper.delByRoleIdAndUserIdAndUserIdAndTenantId(delSysUserRoleVo.getRoleId(), userIds,
				loginInfo.getTenantId());
		return true;
	}

	@Override
	public boolean removeRole(Long roleId) {
		LambdaQueryWrapper<SysUserRole> userRoleLambdaQueryWrapper = Wrappers.lambdaQuery();
		userRoleLambdaQueryWrapper.eq(SysUserRole::getRoleId, roleId);
		List<SysUserRole> sysUserRoles = baseMapper.selectList(userRoleLambdaQueryWrapper);
		if (sysUserRoles.size()>0){
			throw new BusinessException("角色已绑定员工，请先解除员工授权");
		}
		if (CollectionUtils.isNotEmpty(sysUserRoles)) {
			return remove(userRoleLambdaQueryWrapper);
		}
		return true;
	}

	@Override
	public boolean removeByUserId(Long userId) {
		LambdaQueryWrapper<SysUserRole> userRoleLambdaQueryWrapper = Wrappers.lambdaQuery();
		userRoleLambdaQueryWrapper.eq(SysUserRole::getUserId, userId);
		boolean remove = remove(userRoleLambdaQueryWrapper);

		return remove;
	}
}
