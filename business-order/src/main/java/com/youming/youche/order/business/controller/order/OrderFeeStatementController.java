package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOrderFeeStatementService;
import com.youming.youche.order.domain.order.OrderFeeStatement;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 订单账单信息表 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-20
 */
@RestController
@RequestMapping("order-fee-statement")
public class OrderFeeStatementController extends BaseController<OrderFeeStatement, IOrderFeeStatementService> {
    @DubboReference(version = "1.0.0")
    IOrderFeeStatementService orderFeeStatementService;
    @Override
    public IOrderFeeStatementService getService() {
        return orderFeeStatementService;
    }
}
