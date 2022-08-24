package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOrderFeeHService;
import com.youming.youche.order.domain.order.OrderFeeH;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 订单费用历史表 前端控制器
 * </p>
 *
 * @author chenzhe
 * @since 2022-03-21
 */
@RestController
@RequestMapping("order/fee/h")
public class OrderFeeHController extends BaseController<OrderFeeH, IOrderFeeHService> {

    @DubboReference(version = "1.0.0")
    IOrderFeeHService orderFeeHService;

    @Override
    public IOrderFeeHService getService() {
        return orderFeeHService;
    }
}
