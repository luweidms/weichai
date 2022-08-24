package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOrderAccountOilSourceService;
import com.youming.youche.order.domain.order.OrderAccountOilSource;
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
 * @since 2022-03-22
 */
@RestController
@RequestMapping("order-account-oil-source")
public class OrderAccountOilSourceController extends BaseController<OrderAccountOilSource, IOrderAccountOilSourceService> {
    @DubboReference(version = "1.0.0")
    IOrderAccountOilSourceService orderAccountOilSourceService;
    @Override
    public IOrderAccountOilSourceService getService() {
        return orderAccountOilSourceService;
    }
}
