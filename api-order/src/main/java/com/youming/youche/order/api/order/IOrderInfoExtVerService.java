package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderInfoExtVer;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-21
 */
public interface IOrderInfoExtVerService extends IBaseService<OrderInfoExtVer> {
    /**
     * 通过orderId查询OrderInfoExtVer 获取最大值
     * @param orderId
     * @return
     */
    OrderInfoExtVer getOrderInfoExtVer(Long orderId);
}
