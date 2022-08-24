package com.youming.youche.system.business.controller;

import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseCode;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.ISysMenuService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.domain.SysMenu;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 系统菜单表 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController extends BaseController<SysMenu, ISysMenuService> {

	@DubboReference(version = "1.0.0")
	ISysMenuService sysMenuService;

	@DubboReference(version = "1.0.0")
	ISysUserService sysUserService;

	@Override
	public ISysMenuService getService() {
		return sysMenuService;
	}

	/**
	* 方法实现说明 查询用户所拥有的菜单及按钮权限
	 * 返回树状接口
	* @author      terry
	* @return
	* @exception
	* @date        2022/5/31 11:47
	*/
	@GetMapping({ "get" })
	public ResponseResult get() {
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		SysUser sysUser = sysUserService.get(accessToken);
		return ResponseResult.success(sysMenuService.getAll(sysUser.getId(),accessToken));
	}

	/**
	 * 方法实现说明 查询角色所拥有的菜单及按钮权限
	 * @author      terry
	 * @param roleId
	 * @return      com.youming.youche.commons.response.ResponseResult
	 * @exception
	 * @date        2022/5/31 11:48
	 */
	@GetMapping({ "getRole/{roleId}" })
	public ResponseResult getRoleId(@PathVariable("roleId") Long roleId) {
		return ResponseResult.success(sysMenuService.getRoleId(roleId));
	}

	/**
	 * 方法实现说明 查询所有菜单及按钮权限
	 * 返回树状接口
	 * @author      terry
	 * @return
	 * @exception
	 * @date        2022/5/31 11:47
	 */
	@GetMapping({ "getAll" })
	public ResponseResult getAll() {
		return ResponseResult.success(sysMenuService.getAll());
	}

}
