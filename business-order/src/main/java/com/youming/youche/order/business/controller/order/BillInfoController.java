package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IBillInfoService;
import com.youming.youche.order.domain.order.BillInfo;
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
 * @since 2022-03-19
 */
@RestController
@RequestMapping("bill-info")
public class BillInfoController extends BaseController<BillInfo, IBillInfoService> {
    @DubboReference(version = "1.0.0")
    IBillInfoService billInfoService;
    @Override
    public IBillInfoService getService() {
        return billInfoService;
    }


}
