package com.youming.youche.system.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.response.ResponseCode;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.ISysRoleService;
import com.youming.youche.system.aspect.SysOperatorSaveLog;
import com.youming.youche.system.business.dto.BusinessIdDto;
import com.youming.youche.system.domain.SysRole;
import com.youming.youche.system.dto.SysRoleDto;
import com.youming.youche.system.vo.CreateRoleMenuVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>
 * 系统角色表;一个角色可以关联多个操作员，一个操作员可以有多个角色。;一个角色可以关联多个权限，一个权限可以被多个角色拥有。 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
@RestController
@RequestMapping("/sys/role")
public class SysRoleController extends BaseController<SysRole, ISysRoleService> {

	@DubboReference(version = "1.0.0", retries = 0)
	ISysRoleService sysRoleService;

	@Override
	public ISysRoleService getService() {
		return sysRoleService;
	}

	/**
	 * 方法实现说明  查询所有角色
	 * @author      terry
	 * @param pageNum
	 * @param pageSize
	 * @param roleName 角色名
	 * @param opName 操作人
	 * @return      com.youming.youche.commons.response.ResponseResult
	 * @exception
	 * @date        2022/5/31 14:04
	 */
	@GetMapping({ "getAll" })
	public ResponseResult getAll(@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
								 @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize,
								 @RequestParam(value = "roleName", required = false) String roleName,
									 @RequestParam(value = "opName", required = false) String opName) {
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		IPage<SysRoleDto> all = sysRoleService.getAll(accessToken, pageNum, pageSize, roleName, opName);
		return ResponseResult.success(all);
	}
	/**
	 * 方法实现说明  查询当前用户的角色
	 * @author      terry
	 * @return      com.youming.youche.commons.response.ResponseResult
	 * @exception
	 * @date        2022/5/31 14:04
	 */
	@GetMapping({ "get" })
	public ResponseResult getOwn() {
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		return ResponseResult.success(sysRoleService.getOwn(accessToken));
	}

	/**
	 * 方法实现说明 新增角色
	 * @author      terry
	 * @param createRoleMenuVo
	 * @return      com.youming.youche.commons.response.ResponseResult
	 * @exception
	 * @date        2022/5/31 14:06
	 */
	@PostMapping({ "creates" })
	@SysOperatorSaveLog(code = SysOperLogConst.BusiCode.Entity, type = SysOperLogConst.OperType.Add, comment = "新增角色信息")
	public ResponseResult create(@Valid @RequestBody CreateRoleMenuVo createRoleMenuVo) {

		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		Long created = sysRoleService.create(createRoleMenuVo, accessToken);
		return ResponseResult.success(BusinessIdDto.of().setId(created));
	}

	/**
	 * 方法实现说明 根据角色id删除
	 * @author      terry
	 * @param Id
	 * @return      com.youming.youche.commons.response.ResponseResult
	 * @exception
	 * @date        2022/5/31 14:06
	 */
	@Override
	@SysOperatorSaveLog(code = SysOperLogConst.BusiCode.Entity, type = SysOperLogConst.OperType.Del, comment = "删除角色信息")
	public ResponseResult remove(@PathVariable Long Id) {

		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		boolean deleted = sysRoleService.remove(Id, accessToken);
		return deleted ? ResponseResult.success(BusinessIdDto.of().setId(Id))
				: ResponseResult.failure(ResponseCode.INTERFACE_ADDRESS_INVALID);
	}

}
