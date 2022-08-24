package com.youming.youche.system.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseCode;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.ISysUserRoleService;
import com.youming.youche.system.domain.SysUserRole;
import com.youming.youche.system.dto.OrganizeStaffDto;
import com.youming.youche.system.vo.CreateSysUserRoleVo;
import com.youming.youche.system.vo.DelSysUserRoleVo;
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
import java.util.List;

/**
 * <p>
 * 系统角色操作员关系表;ROLE_ID : 对应sys_role中的role_id;op_id : 对应sys_operator中的operator_id 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
@RestController
@RequestMapping("/sys/user/role")
public class SysUserRoleController extends BaseController<SysUserRole, ISysUserRoleService> {

	@DubboReference(version = "1.0.0")
	ISysUserRoleService sysUserRoleService;

	@Override
	public ISysUserRoleService getService() {
		return sysUserRoleService;
	}

	/**
	 * 方法实现说明 用户与角色绑定
	 * @author      terry
	 * @param domain
	 * @return      com.youming.youche.commons.response.ResponseResult
	 * @exception
	 * @date        2022/5/31 15:43
	 */
	@PostMapping({ "creates" })
	public ResponseResult creates(@Valid @RequestBody CreateSysUserRoleVo domain) {
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		boolean created = sysUserRoleService.create(domain, accessToken);
		return created ? ResponseResult.success("授权成功") : null;
	}

	/**
	 * 方法实现说明 查询角色列表
	 * @author      terry
	 * @param pageNum
	 * @param pageSize
	 * @param roleId 角色id
	 * @param linkman 姓名
	 * @param loginacct 登录账号
	 * @param staffPosition 员工id
	 * @return      com.youming.youche.commons.response.ResponseResult
	 * @exception
	 * @date        2022/5/31 15:44
	 */
	@GetMapping({ "get" })
	public ResponseResult getRole(
			@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
			@RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize,
			@RequestParam(value = "roleId") Long roleId,
			@RequestParam(value = "linkman", defaultValue = "", required = false) String linkman,
			@RequestParam(value = "loginacct", defaultValue = "", required = false) String loginacct,
			@RequestParam(value = "staffPosition", defaultValue = "", required = false) String staffPosition) {

		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		IPage<OrganizeStaffDto> role = sysUserRoleService.getRole(roleId, linkman, loginacct, staffPosition, pageNum,
				pageSize, accessToken);
		return ResponseResult.success(role);
	}

	/**
	 * 方法实现说明 用户与角色解绑
	 * @author      terry
	 * @param delSysUserRoleVo
	 * @return      com.youming.youche.commons.response.ResponseResult
	 * @exception
	 * @date        2022/5/31 15:47
	 */
	@DeleteMapping({ "remove" })
	public ResponseResult removes(@Valid @RequestBody DelSysUserRoleVo delSysUserRoleVo) {
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		boolean deleted = sysUserRoleService.remove(delSysUserRoleVo, accessToken);
		return deleted ? ResponseResult.success("删除成功")
				: ResponseResult.failure(ResponseCode.INTERFACE_ADDRESS_INVALID);
	}

}
