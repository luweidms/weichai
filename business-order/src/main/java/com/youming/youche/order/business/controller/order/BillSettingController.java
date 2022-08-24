package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IBillSettingService;
import com.youming.youche.order.domain.order.BillSetting;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 车队的开票设置 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@RestController
@RequestMapping("bill-setting")
public class BillSettingController extends BaseController<BillSetting, IBillSettingService> {
    @DubboReference(version = "1.0.0")
    IBillSettingService billSettingService;
    @Override
    public IBillSettingService getService() {
        return billSettingService;
    }
}
