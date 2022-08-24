package com.youming.youche.order.business.controller.order;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.order.api.order.IOrderRetrographyCostInfoService;
import com.youming.youche.order.domain.OrderRetrographyCostInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 订单校准里程表 前端控制器
 * </p>
 *
 * @author chenzhe
 * @since 2022-03-20
 */
@RestController
@RequestMapping("order/retrography/cost/info")
public class OrderRetrographyCostInfoController extends BaseController<OrderRetrographyCostInfo, IOrderRetrographyCostInfoService> {

    @DubboReference(version = "1.0.0")
    IOrderRetrographyCostInfoService orderRetrographyCostInfoService;

    @Override
    public IOrderRetrographyCostInfoService getService() {

        return orderRetrographyCostInfoService;
    }
}
