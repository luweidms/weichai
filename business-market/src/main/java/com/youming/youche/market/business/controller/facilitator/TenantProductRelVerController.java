package com.youming.youche.market.business.controller.facilitator;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.market.api.facilitator.ITenantProductRelVerService;
import com.youming.youche.market.domain.facilitator.TenantProductRelVer;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 租户与站点关系表 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-29
 */
@RestController
@RequestMapping("/facilitator/tenant-product-rel-ver")
public class TenantProductRelVerController extends BaseController<TenantProductRelVer, ITenantProductRelVerService> {
    @DubboReference(version = "1.0.0")
    ITenantProductRelVerService tenantProductRelVerService;

    @Override
    public ITenantProductRelVerService getService() {
        return tenantProductRelVerService;
    }
}
