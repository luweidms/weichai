package com.youming.youche.order.business.controller.order;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.order.api.order.IPayoutOrderService;
import com.youming.youche.order.domain.order.PayoutOrder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
@RestController
@RequestMapping("payout-order")
public class PayoutOrderController extends BaseController<PayoutOrder, IPayoutOrderService> {
    @DubboReference(version = "1.0.0")
    IPayoutOrderService payoutOrderService;
    @Override
    public IPayoutOrderService getService() {
        return payoutOrderService;
    }
}
