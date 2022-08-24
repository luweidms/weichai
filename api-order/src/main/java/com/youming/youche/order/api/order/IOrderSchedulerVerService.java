package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderSchedulerVer;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-21
 */
public interface IOrderSchedulerVerService extends IBaseService<OrderSchedulerVer> {
    /**
     * 通过orderid获取 最大的OrderSchedulerVer
     * @param orderId
     * @return
     */
    OrderSchedulerVer getOrderSchedulerVer(Long orderId);

}
