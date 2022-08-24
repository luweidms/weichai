package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOrderFeeStatementHService;
import com.youming.youche.order.domain.order.OrderFeeStatementH;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 订单账单信息历史表 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-20
 */
@RestController
@RequestMapping("order-fee-statement-h")
public class OrderFeeStatementHController {
    @DubboReference(version = "1.0.0")
    IOrderFeeStatementHService orderFeeStatementHService;

    public IOrderFeeStatementHService getService() {
        return orderFeeStatementHService;
    }
}
