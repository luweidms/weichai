package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IRateService;
import com.youming.youche.order.domain.order.Rate;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 费率设置 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@RestController
@RequestMapping("rate")
public class RateController extends BaseController<Rate, IRateService> {
    @DubboReference(version = "1.0.0")
    IRateService rateService;
    @Override
    public IRateService getService() {
        return rateService;
    }
}
