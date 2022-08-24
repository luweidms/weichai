package com.youming.youche.market.business.controller.facilitator;


import com.youming.youche.market.api.facilitator.IRateService;
import com.youming.youche.market.domain.facilitator.Rate;
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
 * @since 2022-02-14
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
