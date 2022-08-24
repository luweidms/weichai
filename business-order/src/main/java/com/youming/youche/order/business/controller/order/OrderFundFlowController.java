package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOrderFundFlowService;
import com.youming.youche.order.domain.order.OrderFundFlow;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
@RestController
@RequestMapping("order-fund-flow")
public class OrderFundFlowController extends BaseController<OrderFundFlow, IOrderFundFlowService> {
    @DubboReference(version = "1.0.0")
    IOrderFundFlowService orderFundFlowService;
    @Override
    public IOrderFundFlowService getService() {
        return orderFundFlowService;
    }
}
