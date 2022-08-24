package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOrderFeeVerService;
import com.youming.youche.order.domain.order.OrderFeeVer;
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
@RequestMapping("order-fee-ver")
public class OrderFeeVerController extends BaseController<OrderFeeVer, IOrderFeeVerService> {
    @DubboReference(version = "1.0.0")
    IOrderFeeVerService orderFeeVerService;
    @Override
    public IOrderFeeVerService getService() {
        return orderFeeVerService;
    }
}
