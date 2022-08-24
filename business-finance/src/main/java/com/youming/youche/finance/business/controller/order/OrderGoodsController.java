package com.youming.youche.finance.business.controller.order;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.finance.api.order.IOrderGoodsService;
import com.youming.youche.finance.domain.order.OrderGoods;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>
* 订单货物表 前端控制器
* </p>
* @author liangyan
* @since 2022-03-15
*/
@RestController
@RequestMapping("order/goods")
public class OrderGoodsController extends BaseController<OrderGoods, IOrderGoodsService> {

    @Override
    public IOrderGoodsService getService() {
        return null;
    }
}
