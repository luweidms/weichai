package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IBaseBillInfoService;
import com.youming.youche.order.domain.order.BaseBillInfo;
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
 * @since 2022-03-24
 */
@RestController
@RequestMapping("base-bill-info")
public class BaseBillInfoController extends BaseController<BaseBillInfo, IBaseBillInfoService>{
    @DubboReference(version = "1.0.0")
    IBaseBillInfoService baseBillInfoService;
    @Override
    public IBaseBillInfoService getService() {
        return baseBillInfoService;
    }
}
