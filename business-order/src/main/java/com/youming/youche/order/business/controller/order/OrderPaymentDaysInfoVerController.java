package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOrderPaymentDaysInfoVerService;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfoVer;
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
@RequestMapping("order-payment-days-info-ver")
public class OrderPaymentDaysInfoVerController extends BaseController<OrderPaymentDaysInfoVer, IOrderPaymentDaysInfoVerService> {
    @DubboReference(version = "1.0.0")
    IOrderPaymentDaysInfoVerService orderPaymentDaysInfoVerService;
    @Override
    public IOrderPaymentDaysInfoVerService getService() {
        return orderPaymentDaysInfoVerService;
    }
}
