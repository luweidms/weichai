package com.youming.youche.system.api;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.system.domain.SysMenu;

import java.util.List;

/**
 * <p>
 * 系统菜单表 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
public interface ISysMenuService extends IBaseService<SysMenu> {

	/**
	 * 方法实现说明 获取userId拥有的菜单
	 * @author terry
	 * @param userId
	 * @return java.util.List<com.youming.youche.system.domain.SysMenu>
	 * @exception
	 * @date 2021/12/30 12:00
	 */
	List<SysMenu> getAll(long userId,String accessToken);
	/**
	* 方法实现说明  获取userId拥有的菜单
	* @author      terry
	* @param loginInfo  登录者信息
	* @return
	* @exception
	* @date        2022/5/17 13:59
	*/
	List<SysMenu> getAll(LoginInfo loginInfo);

	/**
	 * 方法实现说明 获取系统全部菜单
	 * @author terry
	 * @return java.util.List<com.youming.youche.system.domain.SysMenu>
	 * @exception
	 * @date 2021/12/30 12:00
	 */
	List<SysMenu> getAll();

	/**
	 * 方法实现说明 查询角色所拥有的菜单及按钮权限
	 * @author      terry
	 * @param roleId
	 * @return      com.youming.youche.commons.response.ResponseResult
	 * @exception
	 * @date        2022/5/31 11:48
	 */
	List<SysMenu> getRoleId(Long roleId);

}
