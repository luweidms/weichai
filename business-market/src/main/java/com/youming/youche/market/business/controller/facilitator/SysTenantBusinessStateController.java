package com.youming.youche.market.business.controller.facilitator;


import com.youming.youche.market.api.facilitator.ISysTenantBusinessStateService;
import com.youming.youche.market.domain.facilitator.SysTenantBusinessState;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 车队经营状况 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-14
 */
@RestController
@RequestMapping("sys-tenant-business-state")
public class SysTenantBusinessStateController extends BaseController<SysTenantBusinessState, ISysTenantBusinessStateService> {
    @DubboReference(version = "1.0.0")
    ISysTenantBusinessStateService sysTenantBusinessStateService;

    @Override
    public ISysTenantBusinessStateService getService() {
        return sysTenantBusinessStateService;
    }
}
