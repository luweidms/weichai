package com.youming.youche.order.business.controller.order;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.order.api.order.IOrderFeeExtService;
import com.youming.youche.order.api.order.IOrderGoodsService;
import com.youming.youche.order.domain.order.OrderFeeExt;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 订单费用扩展表 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-15
 */
@RestController
@RequestMapping("order-fee-ext")
public class OrderFeeExtController extends BaseController<OrderFeeExt, IOrderFeeExtService> {
    @DubboReference(version = "1.0.0")
    IOrderFeeExtService orderFeeExtService;
    @Override
    public IOrderFeeExtService getService() {
        return orderFeeExtService;
    }
}
