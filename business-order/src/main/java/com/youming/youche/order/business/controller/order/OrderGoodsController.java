package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOrderGoodsService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.domain.order.OrderInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 订单货物表 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-15
 */
@RestController
@RequestMapping("order-goods")
public class OrderGoodsController extends BaseController<OrderGoods, IOrderGoodsService> {
    @DubboReference(version = "1.0.0")
    IOrderGoodsService orderGoodsService;
    @Override
    public IOrderGoodsService getService() {
        return orderGoodsService;
    }
}
