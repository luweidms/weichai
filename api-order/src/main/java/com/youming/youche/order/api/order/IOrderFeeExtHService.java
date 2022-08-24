package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderFeeExtH;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
public interface IOrderFeeExtHService extends IBaseService<OrderFeeExtH> {

    /**
     * 获取历史订单信息
     * @param orderId
     * @return
     */
    OrderFeeExtH getOrderFeeExtH(Long orderId);

    /**
     * 获取历史订单信息
     * @param orderId
     * @return
     */
    OrderFeeExtH selectByOrderId(Long orderId);

}
