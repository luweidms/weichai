package com.youming.youche.system.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.ITenantStaffRelService;
import com.youming.youche.system.domain.TenantStaffRel;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 员工信息 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-01-04
 */
@RestController
@RequestMapping("/tenant/staff")
public class TenantStaffRelController extends BaseController<TenantStaffRel, ITenantStaffRelService> {

	@DubboReference(version = "1.0.0")
	ITenantStaffRelService tenantStaffRelService;

	@Override
	public ITenantStaffRelService getService() {
		return tenantStaffRelService;
	}

	/**
	 * 获取车队全部员工信息
	 * */
	@GetMapping({ "getAlls" })
	public ResponseResult getAlls(
			@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
			@RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize,
			@RequestParam(value = "phone", required = false) String phone,
			@RequestParam(value = "linkman", required = false) String linkman,
			@RequestParam(value = "number", required = false) String number,
			@RequestParam(value = "position", required = false) String position,
			@RequestParam(value = "lockFlag", required = false)  Integer lockFlag,
			@RequestParam(value = "orgId", required = false) String orgId) {
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		// userDataInfoService.
		// return ResponseResult.success(domain);
		return ResponseResult.success(tenantStaffRelService.get(accessToken, pageNum, pageSize, phone, linkman,number,position,lockFlag,orgId));
	}

	/**
	 * 方法实现说明 通过用户信息获取员工信息
	 * @author      terry
	 * @param userInfoId 用户信息id
	 * @return      com.youming.youche.commons.response.ResponseResult
	 * @exception
	 * @date        2022/5/31 15:50
	 */
	@GetMapping({"gets/{userInfoId}"})
	public ResponseResult getByUserInfo(@PathVariable("userInfoId")Long userInfoId) {
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		return ResponseResult.success(tenantStaffRelService.get(accessToken,userInfoId));
	}
	/**
	 * 方法实现说明 通过手机号码获取员工信息
	 * @author      terry
	 * @param phone 手机号码
	 * @return      com.youming.youche.commons.response.ResponseResult
	 * @exception
	 * @date        2022/5/31 15:50
	 */
	@GetMapping({"getPhone/{phone}"})
	public ResponseResult getByUserInfo(@PathVariable("phone")String  phone) {
		return ResponseResult.success(tenantStaffRelService.selectByPhone(phone));
	}

	/**
	 * 实现功能: 查询归属人列表
	 *
	 * @param lockFlag 是否启用（1：启用，2停用）
	 * @param userAccount 员工账号
	 * @param staffName 员工姓名
	 * @return
	 */
	@GetMapping("queryStaffInfo")
	public ResponseResult queryStaffInfo(@RequestParam("lockFlag")Integer lockFlag,
										 @RequestParam("pageNum")Integer pageNum,
										 @RequestParam("pageSize")Integer pageSize,
										 @RequestParam("userAccount")Long userAccount,
										 @RequestParam("staffName")String staffName){
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		Page<TenantStaffRel> page = new Page<>(pageNum,pageSize);
		Page<TenantStaffRel> page1 = tenantStaffRelService.queryStaffInfo(page, lockFlag, accessToken,userAccount,staffName);
		return ResponseResult.success(page1);
	}

}
