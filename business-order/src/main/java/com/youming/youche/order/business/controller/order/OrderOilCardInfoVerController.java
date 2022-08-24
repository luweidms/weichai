package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOrderOilCardInfoVerService;
import com.youming.youche.order.domain.order.OrderOilCardInfoVer;
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
@RequestMapping("order-oil-card-info-ver")
public class OrderOilCardInfoVerController extends BaseController<OrderOilCardInfoVer, IOrderOilCardInfoVerService> {
    @DubboReference(version = "1.0.0")
    IOrderOilCardInfoVerService orderOilCardInfoVerService;
    @Override
    public IOrderOilCardInfoVerService getService() {
        return orderOilCardInfoVerService;
    }
}
