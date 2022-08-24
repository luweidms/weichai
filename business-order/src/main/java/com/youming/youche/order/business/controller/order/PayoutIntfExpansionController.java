package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IPayoutIntfExpansionService;
import com.youming.youche.order.domain.order.PayoutIntfExpansion;
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
 * @since 2022-03-22
 */
@RestController
@RequestMapping("payout-intf-expansion")
public class PayoutIntfExpansionController extends BaseController<PayoutIntfExpansion, IPayoutIntfExpansionService> {
    @DubboReference(version = "1.0.0")
    IPayoutIntfExpansionService payoutIntfExpansionService;
    @Override
    public IPayoutIntfExpansionService getService() {
        return payoutIntfExpansionService;
    }
}
