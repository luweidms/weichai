package com.youming.youche.system.api;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.system.domain.SysPermission;
import com.youming.youche.system.vo.UpdatePermissionVo;

import java.util.List;

/**
 * <p>
 * 权限表 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
public interface ISysPermissionService extends IBaseService<SysPermission> {

	/**
	 * 方法实现说明 获取userId的权限
	 * @param roleId 用户角色id
     * @return java.util.List<com.youming.youche.system.domain.SysPermission>
	 * @throws
	 * @author terry
	 * @date 2021/12/30 11:57
	 */
	List<SysPermission> getRole(Long roleId);

	/**
	 * 方法实现说明 获取userId的权限
	 * @param roleId 用户角色id
	 * @param type
	 * @return java.util.List<com.youming.youche.system.domain.SysPermission>
	 * @throws
	 * @author terry
	 * @date 2021/12/30 11:57
	 */
	List<SysPermission> getRole(Long roleId, Integer type);

	/**
	 * 方法实现说明 修改角色权限
	 * @author      terry
	 * @param permissionVo
	 * @return      boolean
	 * @exception
	 * @date        2022/5/31 13:43
	 */
    boolean updateAll(UpdatePermissionVo permissionVo);
}
