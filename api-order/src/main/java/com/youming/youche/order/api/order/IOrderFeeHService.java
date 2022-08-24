package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderFeeH;

/**
* <p>
    * 订单费用历史表 服务类
    * </p>
* @author chenzhe
* @since 2022-03-21
*/
    public interface IOrderFeeHService extends IBaseService<OrderFeeH> {

    /**
     * 获取历史订单费用
     * @param orderId
     * @return
     */
    OrderFeeH selectByOrderId(Long orderId);

    /**
     * 通过id获取订单历史信息
     *
     * @param orderId 订单id
     * @return
     */
    OrderFeeH getOrderFeeH(Long orderId);

}
