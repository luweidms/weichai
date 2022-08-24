package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOrderTransitLineInfoHService;
import com.youming.youche.order.domain.order.OrderTransitLineInfoH;
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
@RequestMapping("order-transit-line-info-h")
public class OrderTransitLineInfoHController extends BaseController<OrderTransitLineInfoH, IOrderTransitLineInfoHService> {
    @DubboReference(version = "1.0.0")
    IOrderTransitLineInfoHService orderTransitLineInfoHService;
    @Override
    public IOrderTransitLineInfoHService getService() {
        return orderTransitLineInfoHService;
    }
}
