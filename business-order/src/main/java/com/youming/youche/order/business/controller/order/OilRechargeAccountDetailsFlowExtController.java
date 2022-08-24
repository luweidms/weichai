package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOilRechargeAccountDetailsFlowExtService;
import com.youming.youche.order.domain.order.OilRechargeAccountDetailsFlowExt;
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
@RequestMapping("oil-recharge-account-details-flow-ext")
public class OilRechargeAccountDetailsFlowExtController extends BaseController<OilRechargeAccountDetailsFlowExt, IOilRechargeAccountDetailsFlowExtService> {
    @DubboReference(version = "1.0.0")
    IOilRechargeAccountDetailsFlowExtService oilRechargeAccountDetailsFlowExtService;
    @Override
    public IOilRechargeAccountDetailsFlowExtService getService() {
        return oilRechargeAccountDetailsFlowExtService;
    }
}
