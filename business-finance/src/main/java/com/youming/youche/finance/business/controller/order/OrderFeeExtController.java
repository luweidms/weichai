package com.youming.youche.finance.business.controller.order;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.finance.api.order.IOrderFeeExtService;
import com.youming.youche.finance.domain.order.OrderFeeExt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>
* 订单费用扩展表 前端控制器
* </p>
* @author liangyan
* @since 2022-03-15
*/
@RestController
@RequestMapping("order-fee-ext")
public class OrderFeeExtController extends BaseController<OrderFeeExt, IOrderFeeExtService> {

    @Override
    public IOrderFeeExtService getService() {
        return null;
    }
}
