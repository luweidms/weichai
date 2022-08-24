package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderInfoVer;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-21
 */
public interface IOrderInfoVerService extends IBaseService<OrderInfoVer> {

    /**
     * 获取订单信息
     * @param orderId
     * @return
     */
    OrderInfoVer getOrderInfoVer(Long orderId);
}
