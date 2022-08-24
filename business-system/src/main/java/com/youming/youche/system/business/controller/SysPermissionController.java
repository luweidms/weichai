package com.youming.youche.system.business.controller;

import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.system.api.ISysPermissionService;
import com.youming.youche.system.aspect.SysOperatorSaveLog;
import com.youming.youche.system.business.dto.BusinessIdDto;
import com.youming.youche.system.domain.SysPermission;
import com.youming.youche.system.vo.UpdatePermissionVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 权限表 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
@RestController
@RequestMapping("sys/permission")
public class SysPermissionController extends BaseController<SysPermission, ISysPermissionService> {

	@DubboReference(version = "1.0.0")
	ISysPermissionService sysPermissionService;

	@Override
	public ISysPermissionService getService() {
		return sysPermissionService;
	}

	/**
	 * 方法实现说明  修改角色权限
	 * @author      terry
	 * @param permissionVo
	 * @return      com.youming.youche.commons.response.ResponseResult
	 * @exception
	 * @date        2022/5/31 13:42
	 */
	@PutMapping({ "updateAll" })
	@SysOperatorSaveLog(code = SysOperLogConst.BusiCode.Entity, type = SysOperLogConst.OperType.Update, comment = "修改角色信息")
	public ResponseResult updateAll(@RequestBody UpdatePermissionVo permissionVo) {
		sysPermissionService.updateAll(permissionVo);
		return ResponseResult.success(BusinessIdDto.of().setId(permissionVo.getRoleId()));
	}

}
