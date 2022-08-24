package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IServiceMatchOrderService;
import com.youming.youche.order.domain.order.ServiceMatchOrder;
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
@RequestMapping("service-match-order")
public class ServiceMatchOrderController extends BaseController<ServiceMatchOrder, IServiceMatchOrderService> {
    @DubboReference(version = "1.0.0")
    IServiceMatchOrderService serviceMatchOrderService;
    @Override
    public IServiceMatchOrderService getService() {
        return serviceMatchOrderService;
    }
}
