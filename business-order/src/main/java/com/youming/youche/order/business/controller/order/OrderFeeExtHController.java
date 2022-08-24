package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOrderFeeExtHService;
import com.youming.youche.order.domain.order.OrderFeeExtH;
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
 * @since 2022-03-19
 */
@RestController
@RequestMapping("order-fee-ext-h")
public class OrderFeeExtHController extends BaseController<OrderFeeExtH, IOrderFeeExtHService> {
    @DubboReference(version = "1.0.0")
    IOrderFeeExtHService orderFeeExtHService;
    @Override
    public IOrderFeeExtHService getService() {
        return orderFeeExtHService;
    }
}
