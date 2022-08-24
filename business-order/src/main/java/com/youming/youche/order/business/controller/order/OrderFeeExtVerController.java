package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOrderFeeExtVerService;
import com.youming.youche.order.domain.order.OrderFeeExtVer;
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
@RequestMapping("order-fee-ext-ver")
public class OrderFeeExtVerController extends BaseController<OrderFeeExtVer, IOrderFeeExtVerService> {
    @DubboReference(version = "1.0.0")
    IOrderFeeExtVerService orderFeeExtVerService;
    @Override
    public IOrderFeeExtVerService getService() {
        return orderFeeExtVerService;
    }
}
