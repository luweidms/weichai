package com.youming.youche.system.business.controller;

import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.ISysPermissionMenuService;
import com.youming.youche.system.domain.SysPermissionMenu;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 角色权限表 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
@RestController
@RequestMapping("/sys/permission/menu")
public class SysPermissionMenuController extends BaseController<SysPermissionMenu, ISysPermissionMenuService> {

	@DubboReference(version = "1.0.0")
	ISysPermissionMenuService sysPermissionMenuService;

	@Override
	public ISysPermissionMenuService getService() {
		return sysPermissionMenuService;
	}

	/**
	 * 方法实现说明
	 * @author      terry
	 * @param ids 菜单id集合
	 * @param role 角色id
	 * @return      com.youming.youche.commons.response.ResponseResult
	 * @exception
	 * @date        2022/5/31 13:50
	 */
	@PutMapping({ "updateAll/{role}" })
	public ResponseResult updateAll(@RequestBody List<Long> ids,
			@PathVariable(value = "role") Long role) {
		sysPermissionMenuService.updateAll(ids, role);
		return ResponseResult.success();
	}

}
