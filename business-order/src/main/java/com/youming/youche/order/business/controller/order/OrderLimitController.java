package com.youming.youche.order.business.controller.order;


import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.dto.OrderLimitDto;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 订单付款回显表 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
@RestController
@RequestMapping("order/limit")
public class OrderLimitController extends BaseController<OrderLimit, IOrderLimitService> {
    @DubboReference(version = "1.0.0")
    IOrderLimitService orderLimitService;
    @Override
    public IOrderLimitService getService() {
        return orderLimitService;
    }

    /**
     * WX接口-校验订单是否可添加异常[30060]
     */
    @GetMapping("hasFinalExpire")
    public ResponseResult hasFinalExpire(Long orderId) {
        OrderLimitDto orderLimitDto = orderLimitService.hasFinalOrderLimit(orderId, -1);
        return ResponseResult.success(orderLimitDto);
    }


}
