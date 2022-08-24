package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOrderOilDepotSchemeVerService;
import com.youming.youche.order.domain.order.OrderOilDepotSchemeVer;
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
 * @since 2022-03-21
 */
@RestController
@RequestMapping("order-oil-depot-scheme-ver")
public class OrderOilDepotSchemeVerController extends BaseController<OrderOilDepotSchemeVer, IOrderOilDepotSchemeVerService> {
    @DubboReference(version = "1.0.0")
    IOrderOilDepotSchemeVerService orderOilDepotSchemeVerService;
    @Override
    public IOrderOilDepotSchemeVerService getService() {
        return orderOilDepotSchemeVerService;
    }
}
