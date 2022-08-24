package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfoH;

/**
 * 订单收入账期历史
 *
 * @author hzx
 * @date 2022/3/23 17:43
 */
public interface IOrderPaymentDaysInfoHService extends IBaseService<OrderPaymentDaysInfoH> {

    /**
     * 订单收入账期历史记录
     *
     * @param orderId         订单id
     * @param paymentDaysType 账期类型:1 成本 2 收入
     */
    OrderPaymentDaysInfoH queryOrderPaymentDaysInfoH(Long orderId, Integer paymentDaysType);

}
