package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOrderTransitLineInfoService;
import com.youming.youche.order.domain.order.OrderTransitLineInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 订单途径表 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-17
 */
@RestController
@RequestMapping("order-transit-line-info")
public class OrderTransitLineInfoController extends BaseController<OrderTransitLineInfo, IOrderTransitLineInfoService> {
    @DubboReference(version = "1.0.0")
    IOrderTransitLineInfoService orderTransitLineInfoService;

    @Override
    public IOrderTransitLineInfoService getService() {
        return orderTransitLineInfoService;
    }
}
