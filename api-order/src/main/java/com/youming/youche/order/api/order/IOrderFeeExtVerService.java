package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderFeeExtVer;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-21
 */
public interface IOrderFeeExtVerService extends IBaseService<OrderFeeExtVer> {

    /**
     * 获取订单费用扩展版本信息
     * @param orderId
     * @return
     */
    OrderFeeExtVer getOrderFeeExtVer(Long orderId);

}
