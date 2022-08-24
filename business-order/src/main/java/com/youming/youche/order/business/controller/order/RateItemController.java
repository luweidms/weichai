package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IRateItemService;
import com.youming.youche.order.domain.order.RateItem;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 费率设置项（字表） 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@RestController
@RequestMapping("rate-item")
public class RateItemController extends BaseController<RateItem, IRateItemService> {
    @DubboReference(version = "1.0.0")
    IRateItemService rateItemService;
    @Override
    public IRateItemService getService() {
        return rateItemService;
    }
}
