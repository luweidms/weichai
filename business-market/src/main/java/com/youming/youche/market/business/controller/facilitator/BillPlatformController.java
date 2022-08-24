package com.youming.youche.market.business.controller.facilitator;


import com.youming.youche.market.api.facilitator.IBillPlatformService;
import com.youming.youche.market.domain.facilitator.BillPlatform;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 票据平台表 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-14
 */
@RestController
@RequestMapping("bill-platform")
public class BillPlatformController extends BaseController<BillPlatform, IBillPlatformService> {
    @DubboReference(version = "1.0.0")
    IBillPlatformService billPlatformService;
    @Override
    public IBillPlatformService getService() {
        return billPlatformService;
    }
}
