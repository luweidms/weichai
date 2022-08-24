package com.youming.youche.system.business.controller;

import com.youming.youche.commons.base.BaseController;
import com.youming.youche.system.api.ISysMenuUrlService;
import com.youming.youche.system.domain.SysMenuUrl;
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
@RequestMapping("/sys/menu/url")
@Deprecated
public class SysMenuUrlController extends BaseController<SysMenuUrl, ISysMenuUrlService> {

	@DubboReference
	ISysMenuUrlService sysMenuUrlService;

	@Override
	public ISysMenuUrlService getService() {
		return sysMenuUrlService;
	}

}
