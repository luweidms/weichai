package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.ITenantAgentServiceRelService;
import com.youming.youche.order.domain.order.TenantAgentServiceRel;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@RestController
@RequestMapping("tenant-agent-service-rel")
public class TenantAgentServiceRelController extends BaseController<TenantAgentServiceRel, ITenantAgentServiceRelService> {
    @DubboReference(version = "1.0.0")
    ITenantAgentServiceRelService tenantAgentServiceRelService;
    @Override
    public ITenantAgentServiceRelService getService() {
        return tenantAgentServiceRelService;
    }
}
