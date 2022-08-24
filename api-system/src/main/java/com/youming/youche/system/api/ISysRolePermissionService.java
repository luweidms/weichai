package com.youming.youche.system.api;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.system.domain.SysRolePermission;

import java.util.List;

/**
 * <p>
 * 角色权限表 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
public interface ISysRolePermissionService extends IBaseService<SysRolePermission> {

	/**
	 * 方法实现说明 查询角色所有的权限
	 * @author      terry
	 * @param roleId
	 * @return      java.util.List<com.youming.youche.system.domain.SysRolePermission>
	 * @exception
	 * @date        2022/5/31 14:16
	 */
	List<SysRolePermission> selectAllByRoleId(Long roleId);

	/**
	 * 方法实现说明 解绑角色与权限绑定
	 * @author      terry
	 * @param roleId
	 * @return      boolean
	 * @exception
	 * @date        2022/5/31 14:16
	 */
	boolean removeRole(Long roleId);

}
