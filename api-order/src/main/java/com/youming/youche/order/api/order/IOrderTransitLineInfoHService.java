package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderTransitLineInfoH;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-21
 */
public interface IOrderTransitLineInfoHService extends IBaseService<OrderTransitLineInfoH> {

    /**
     * 继承来源订单的经停点
     * @param orderId
     * @return
     */
    List<OrderTransitLineInfoH> queryOrderTransitLineInfoHByOrderId(Long orderId);

}
