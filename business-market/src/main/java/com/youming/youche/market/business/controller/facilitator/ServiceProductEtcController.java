package com.youming.youche.market.business.controller.facilitator;


import com.youming.youche.market.api.facilitator.IServiceProductEtcService;
import com.youming.youche.market.domain.facilitator.ServiceProductEtc;
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
 * @since 2022-02-17
 */
@RestController
@RequestMapping("service-product-etc")
public class ServiceProductEtcController  {
    @DubboReference(version = "1.0.0")
    IServiceProductEtcService serviceProductEtcService;

    public IServiceProductEtcService getService() {
        return serviceProductEtcService;
    }
}
