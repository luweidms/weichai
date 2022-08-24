package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.ITenantReceiverRelService;
import com.youming.youche.order.domain.order.TenantReceiverRel;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 车队与收款人的关联关系 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@RestController
@RequestMapping("tenant-receiver-rel")
public class TenantReceiverRelController extends BaseController<TenantReceiverRel, ITenantReceiverRelService> {
    @DubboReference(version = "1.0.0")
    ITenantReceiverRelService tenantReceiverRelService;
    @Override
    public ITenantReceiverRelService getService() {
        return tenantReceiverRelService;
    }
}
