package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IServiceBlanceConfigService;
import com.youming.youche.order.domain.order.ServiceBlanceConfig;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
@RestController
@RequestMapping("service-blance-config")
public class ServiceBlanceConfigController extends BaseController<ServiceBlanceConfig, IServiceBlanceConfigService> {
    @DubboReference(version = "1.0.0")
    IServiceBlanceConfigService serviceBlanceConfigService;
    @Override
    public IServiceBlanceConfigService getService() {
        return serviceBlanceConfigService;
    }
}
