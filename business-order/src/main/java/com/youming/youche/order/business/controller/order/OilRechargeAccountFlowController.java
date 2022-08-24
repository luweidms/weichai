package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOilRechargeAccountFlowService;
import com.youming.youche.order.domain.order.OilRechargeAccountFlow;
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
 * @since 2022-03-23
 */
@RestController
@RequestMapping("oil-recharge-account-flow")
public class OilRechargeAccountFlowController extends BaseController<OilRechargeAccountFlow, IOilRechargeAccountFlowService> {
    @DubboReference(version = "1.0.0")
    IOilRechargeAccountFlowService oilRechargeAccountFlowService;
    @Override
    public IOilRechargeAccountFlowService getService() {
        return oilRechargeAccountFlowService;
    }
}
