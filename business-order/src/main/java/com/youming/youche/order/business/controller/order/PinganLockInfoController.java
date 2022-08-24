package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IPinganLockInfoService;
import com.youming.youche.order.domain.order.PinganLockInfo;
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
@RequestMapping("pingan-lock-info")
public class PinganLockInfoController extends BaseController<PinganLockInfo, IPinganLockInfoService> {
    @DubboReference(version = "1.0.0")
    IPinganLockInfoService pinganLockInfoService;
    @Override
    public IPinganLockInfoService getService() {
        return pinganLockInfoService;
    }
}
