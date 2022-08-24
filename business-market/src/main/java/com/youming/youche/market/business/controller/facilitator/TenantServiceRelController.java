package com.youming.youche.market.business.controller.facilitator;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.market.api.facilitator.ITenantServiceRelService;
import com.youming.youche.market.domain.facilitator.TenantServiceRel;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 服务商与租户关系 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-01-22
 */
@RestController
@RequestMapping("/facilitator/tenant-service-rel")
public class TenantServiceRelController extends BaseController<TenantServiceRel, ITenantServiceRelService> {
    @DubboReference(version = "1.0.0")
    ITenantServiceRelService iTenantServiceRelService;
    @Override
    public ITenantServiceRelService getService() {
        return iTenantServiceRelService;
    }
}
