package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOrderOilDepotSchemeService;
import com.youming.youche.order.domain.order.OrderOilDepotScheme;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 订单油站分配表 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-17
 */
@RestController
@RequestMapping("order-oil-depot-scheme")
public class OrderOilDepotSchemeController extends BaseController<OrderOilDepotScheme, IOrderOilDepotSchemeService> {
    @DubboReference(version = "1.0.0")
    IOrderOilDepotSchemeService orderOilDepotSchemeService;
    @Override
    public IOrderOilDepotSchemeService getService() {
        return orderOilDepotSchemeService;
    }
}
