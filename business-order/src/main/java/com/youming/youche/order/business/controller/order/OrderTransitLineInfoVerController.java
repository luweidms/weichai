package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOrderTransitLineInfoVerService;
import com.youming.youche.order.domain.order.OrderTransitLineInfoVer;
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
@RequestMapping("order-transit-line-info-ver")
public class OrderTransitLineInfoVerController extends BaseController<OrderTransitLineInfoVer, IOrderTransitLineInfoVerService> {
    @DubboReference(version = "1.0.0")
    IOrderTransitLineInfoVerService orderTransitLineInfoVerService;
    @Override
    public IOrderTransitLineInfoVerService getService() {
        return orderTransitLineInfoVerService;
    }
}
