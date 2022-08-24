package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderInfoExtH;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author chenzhe
 * @since 2022-03-20
 */
public interface IOrderInfoExtHService extends IBaseService<OrderInfoExtH> {

    /**
     * 根据订单号获取历史订单扩展记录
     * @param orderId
     * @return
     */
    OrderInfoExtH selectByOrderId(Long orderId);

    /**
     * 订单扩展记录
     *
     * @param orderId 订单id
     * @return
     */
    OrderInfoExtH getOrderInfoExtH(Long orderId);

}
