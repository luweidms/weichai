package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOrderStateTrackOperService;
import com.youming.youche.order.domain.order.OrderStateTrackOper;
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
@RequestMapping("order-state-track-oper")
public class OrderStateTrackOperController extends BaseController<OrderStateTrackOper, IOrderStateTrackOperService> {
    @DubboReference(version = "1.0.0")
    IOrderStateTrackOperService orderStateTrackOperService;
    @Override
    public IOrderStateTrackOperService getService() {
        return orderStateTrackOperService;
    }
}
