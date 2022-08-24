package com.youming.youche.market.business.controller.facilitator;


import com.youming.youche.market.api.facilitator.IRateItemService;
import com.youming.youche.market.domain.facilitator.RateItem;
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
 * @since 2022-02-14
 */
@RestController
@RequestMapping("rateitem/data/info")
public class RateItemController  {
    @DubboReference(version = "1.0.0")
    IRateItemService rateItemService;

    public IRateItemService getService() {
        return rateItemService;
    }
}
