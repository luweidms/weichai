package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderFeeVer;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-21
 */
public interface IOrderFeeVerService extends IBaseService<OrderFeeVer> {

    /**
     * 根据订单号获取历史订单费用
     * @param orderId
     * @return
     */
    OrderFeeVer getOrderFeeVer(Long orderId);
}
