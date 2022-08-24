package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOrderPaymentDaysInfoService;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 订单收入账期表 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-17
 */
@RestController
@RequestMapping("order-payment-days-info")
public class OrderPaymentDaysInfoController extends BaseController<OrderPaymentDaysInfo, IOrderPaymentDaysInfoService> {
    @DubboReference(version = "1.0.0")
    IOrderPaymentDaysInfoService orderPaymentDaysInfoService;
    @Override
    public IOrderPaymentDaysInfoService getService() {
        return orderPaymentDaysInfoService;
    }
}
