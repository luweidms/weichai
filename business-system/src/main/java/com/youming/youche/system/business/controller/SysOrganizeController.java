package com.youming.youche.system.business.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseCode;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.ISysOrganizeService;
import com.youming.youche.system.domain.SysOrganize;
import com.youming.youche.system.dto.SysOrganizeDto;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 组织表 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-01-05
 */
@RestController
@RequestMapping("/sys/organize")
public class SysOrganizeController extends BaseController<SysOrganize, ISysOrganizeService> {

	@DubboReference(version = "1.0.0")
	ISysOrganizeService sysOrganizeService;

	@Override
	public ISysOrganizeService getService() {
		return sysOrganizeService;
	}

	/**
	 * 方法实现说明 新增部门
	 * @author      terry
	 * @param sysOrganize
	 * @return      com.youming.youche.commons.response.ResponseResult
	 * @exception
	 * @date        2022/5/31 13:30
	 */
	@Override
	public ResponseResult create(@Valid @RequestBody SysOrganize sysOrganize) {
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		sysOrganize.setState(1);
		boolean created = sysOrganizeService.create(sysOrganize, accessToken);
		return created ? ResponseResult.success("创建成功") : null;
	}

	/**
	 * 获取当前租户下组织架构树
	 */
	@GetMapping({ "get" })
	public ResponseResult get() {
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		List<SysOrganize> sysOrganizes = sysOrganizeService.querySysOrganizeTree(accessToken, 1, true);
		return ResponseResult.success(sysOrganizes);
	}

	/**
	 * 获取当前租户下组织架构树
	 */
	@GetMapping({ "user" })
	public ResponseResult getUser() {
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		List<SysOrganize> sysOrganizes = sysOrganizeService.selectAllByAccessToken(accessToken);
		return ResponseResult.success(sysOrganizes);
	}

	/**
	 * 获取当前租户全部部门,分页加条件
	 */
	@GetMapping({ "getPageAll" })
	public ResponseResult getAll(@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
								 @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize,
								 @RequestParam(value = "orgName", required = false) String orgName) {
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		IPage<SysOrganize> sysOrganizes = sysOrganizeService.querySysOrganize(accessToken, 1,pageNum,pageSize,orgName);
		return ResponseResult.success(sysOrganizes);
	}
	/**
	 * 获取当前租户全部部门（使用下拉框）
	 */
	@GetMapping({ "getAll" })
	public ResponseResult getAll() {
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		List<SysOrganize> sysOrganizes = sysOrganizeService.querySysOrganize(accessToken, 1);
		return ResponseResult.success(sysOrganizes);
	}

	@Override
	public ResponseResult update(@RequestBody SysOrganize domain) {
		if (StringUtils.isEmpty(domain.getOrgName())) {
			throw new BusinessException("请输入组织名称");
		}
		if (domain.getId() <= 0) {
			throw new BusinessException("找不到当前组织ID");
		}
		if (domain.getParentOrgId() <= 0) {
			throw new BusinessException("找不到上级组织ID");
		}
		if (domain.getUserInfoId() <= 0) {
			throw new BusinessException("找不到部门负责人ID");
		}
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		boolean updated = sysOrganizeService.update(domain, accessToken);
		return updated ? ResponseResult.success("编辑成功")
				: ResponseResult.failure(ResponseCode.INTERFACE_ADDRESS_INVALID);
	}

	// @DeleteMapping({"remove/{Id}"})
	@Override
	public ResponseResult remove(@PathVariable Long Id) {
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		boolean deleted = sysOrganizeService.remove(Id, accessToken);
		return deleted ? ResponseResult.success("删除成功")
				: ResponseResult.failure(ResponseCode.INTERFACE_ADDRESS_INVALID);
	}


//	@GetMapping({"get/{Id}"})
	@Override
	public ResponseResult get(@PathVariable Long Id) {
		SysOrganize sysOrganize = sysOrganizeService.get(Id);
		SysOrganize sysOrganizeParent = sysOrganizeService.get(sysOrganize.getParentOrgId());
		SysOrganizeDto sysOrganizeDto = SysOrganizeDto.of();
		BeanUtil.copyProperties(sysOrganize,sysOrganizeDto);
		sysOrganizeDto.setParentOrgName(sysOrganizeParent.getOrgName());
		return ResponseResult.success(sysOrganizeDto);
	}

	/**
	 * 租户下拉列表
	 * 接口编号 11018
	 */
	@GetMapping("getTenantAllOrg")
	public ResponseResult getTenantAllOrg() {
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		List<SysOrganize> sysOragnizeList = sysOrganizeService.getSysOragnizeList(accessToken);

		return ResponseResult.success(sysOragnizeList);
	}

}
