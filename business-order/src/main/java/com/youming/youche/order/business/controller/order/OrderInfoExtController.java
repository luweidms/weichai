package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOrderInfoExtService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderScheduler;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 订单扩展表 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-15
 */
@RestController
@RequestMapping("order-info-ext")
public class OrderInfoExtController extends BaseController<OrderInfoExt, IOrderInfoExtService> {
    @DubboReference(version = "1.0.0")
    IOrderInfoExtService orderInfoExtService;
    @Override
    public IOrderInfoExtService getService() {
        return orderInfoExtService;
    }
}
