package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOrderInfoExtVerService;
import com.youming.youche.order.domain.order.OrderInfoExtVer;
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
@RequestMapping("order-info-ext-ver")
public class OrderInfoExtVerController extends BaseController<OrderInfoExtVer, IOrderInfoExtVerService> {
    @DubboReference(version = "1.0.0")
    IOrderInfoExtVerService orderInfoExtVerService;
    @Override
    public IOrderInfoExtVerService getService() {
        return orderInfoExtVerService;
    }
}
