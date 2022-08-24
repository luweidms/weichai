package com.youming.youche.system.business.controller;

import com.youming.youche.commons.base.BaseController;
import com.youming.youche.system.api.ISysMenuFunUrlService;
import com.youming.youche.system.api.ISysMenuUrlService;
import com.youming.youche.system.domain.SysMenuFunUrl;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 角色权限表 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
@RestController
@RequestMapping("/sys/menu/fun/url")
public class SysMenuFunUrlController extends BaseController<SysMenuFunUrl, ISysMenuFunUrlService> {

	@DubboReference(version = "1.0.0")
	ISysMenuFunUrlService sysMenuFunUrlService;

	@Override
	public ISysMenuFunUrlService getService() {
		return sysMenuFunUrlService;
	}

}
