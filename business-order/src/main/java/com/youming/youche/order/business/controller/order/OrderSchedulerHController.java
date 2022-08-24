package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.domain.order.OrderSchedulerH;
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
 * @since 2022-03-20
 */
@RestController
@RequestMapping("order-scheduler-h")
public class OrderSchedulerHController extends BaseController<OrderSchedulerH, IOrderSchedulerHService> {
    @DubboReference(version = "1.0.0")
    IOrderSchedulerHService orderSchedulerHService;
    @Override
    public IOrderSchedulerHService getService() {
        return orderSchedulerHService;
    }
}
