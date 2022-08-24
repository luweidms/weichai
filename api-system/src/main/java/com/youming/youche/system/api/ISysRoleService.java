package com.youming.youche.system.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.system.domain.SysRole;
import com.youming.youche.system.dto.SysRoleDto;
import com.youming.youche.system.vo.CreateRoleMenuVo;

import java.util.List;

/**
 * <p>
 * 系统角色表;一个角色可以关联多个操作员，一个操作员可以有多个角色。;一个角色可以关联多个权限，一个权限可以被多个角色拥有。 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
public interface ISysRoleService extends IBaseService<SysRole> {

	/**
	 * 方法实现说明 查询当前车队的所有角色
	 * @author terry
	 * @param accessToken
     * @param pageNum
     * @param pageSize
     * @param roleName
     * @param opName
     * @return java.util.List<com.youming.youche.system.domain.SysRole>
	 * @exception
	 * @date 2022/1/7 14:20
	 */
	IPage<SysRoleDto> getAll(String accessToken, Integer pageNum, Integer pageSize, String roleName, String opName);

	/**
	 * 方法实现说明 查询当前车队的所有角色
	 * @author terry
	 * @param tenantId
	 * @return java.util.List<com.youming.youche.system.domain.SysRole>
	 * @exception
	 * @date 2022/1/7 14:20
	 */
	List<SysRole> getAll(Long tenantId);

	/**
	 * 方法实现说明 查询当前用户的角色
	 * @author terry
	 * @param accessToken
	 * @return java.util.List<com.youming.youche.system.domain.SysRole>
	 * @exception
	 * @date 2022/1/7 14:20
	 */
	List<SysRole> getOwn(String accessToken);

	/**
	 * 方法实现说明 查询当前用户的是否拥有菜单权限
	 * @author terry
	 * @param accessToken
	 * @return java.util.List<com.youming.youche.system.domain.SysRole>
	 * @exception
	 * @date 2022/1/7 14:20
	 */
	Boolean hasPermissionAndMenuId(String accessToken,Long menuId);
	/**
	 * 方法实现说明 查询当前用户的是否拥有按钮权限
	 * @author terry
	 * @param accessToken
	 * @return java.util.List<com.youming.youche.system.domain.SysRole>
	 * @exception
	 * @date 2022/1/7 14:20
	 */
	Boolean hasPermissionAndBtnId(String accessToken,Long btnId);
	/**
	 * 方法实现说明 查询当前用户的是否拥有按钮权限
	 * @author terry
	 * @param loginInfo 登录者信息
	 * @return java.util.List<com.youming.youche.system.domain.SysRole>
	 * @exception
	 * @date 2022/1/7 14:20
	 */
	Boolean hasPermissionAndBtnId(LoginInfo loginInfo, Long btnId);
	/**
	 * 判断当前登录人是否拥有"所有数据"权限
	 * @return true-有权限，false-无权限
	 * @throws Exception
	 */
	Boolean hasAllData(LoginInfo loginInfo);

	/**
	 *判断当前登录人是否拥有"线路成本"权限
	 * true-有权限，false-无权限
	 */
	Boolean hasCostPermission(LoginInfo loginInfo);
	/**
	 *判断当前登录人是否拥有"线路收入"权限
	 * true-有权限，false-无权限
	 */
	Boolean hasIncomePermission(LoginInfo loginInfo);

	/**
	 * TODO 判断当前登录人是否拥有订单的"成本信息"权限
	 * @return true-有权限，false-无权限
	 * @throws Exception
	 */
	Boolean hasOrderCostPermission(LoginInfo user);

	/**
	 * 订单收入权限
	 * @param user
	 * @return
	 */
	Boolean hasorderIncomePermission(LoginInfo user);

	/**
	 * 判断当前登录人是否拥有订单的"收入信息"权限
	 * true-有权限，false-无权限
	 */
	Boolean hasOrderIncomePermission(LoginInfo user);

	/**
	 * 方法实现说明  新增角色并绑定权限
	 * @author      terry
	 * @param createRoleMenuVo {@link CreateRoleMenuVo}
	 * @param accessToken 令牌
	 * @return      java.lang.Long
	 * @exception
	 * @date        2022/5/31 14:11
	 */
	Long create(CreateRoleMenuVo createRoleMenuVo, String accessToken);

	/**
	 * 方法实现说明 删除角色并解绑权限
	 * @author      terry
	 * @param roleId
	* @param accessToken
	 * @return      boolean
	 * @exception
	 * @date        2022/5/31 14:10
	 */
	boolean remove(Long roleId, String accessToken);

	// String getOther(Long roleId);

}
