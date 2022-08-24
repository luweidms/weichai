package com.youming.youche.system.api;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.system.domain.SysPermissionMenu;

import java.util.List;

/**
 * <p>
 * 角色权限表 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
public interface ISysPermissionMenuService extends IBaseService<SysPermissionMenu> {

	/**
	 * 方法实现说明 修改角色菜单权限
	 * @author      terry
	 * @param ids
	* @param role
	 * @return      boolean
	 * @exception
	 * @date        2022/5/31 13:52
	 */
	boolean updateAll(List<Long> ids, Long role);

	/**
	 * 方法实现说明 将权限id绑定菜单权限
	 * @author      terry
	 * @param ids
	* @param permissionId
	 * @return      boolean
	 * @exception
	 * @date        2022/5/31 13:53
	 */
	boolean creates(List<Long> ids, Long permissionId);

	/**
	 * 方法实现说明 将权限id解绑菜单权限
	 * @author      terry
	* @param permissionId
	 * @return      boolean
	 * @exception
	 * @date        2022/5/31 13:53
	 */
	boolean removePermissionId(Long roleId);

}
