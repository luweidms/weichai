package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.order.OrderOilDepotScheme;
import com.youming.youche.order.domain.order.OrderOilDepotSchemeVer;

import java.util.List;

/**
 * <p>
 * 订单油站分配表 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-17
 */
public interface IOrderOilDepotSchemeService extends IBaseService<OrderOilDepotScheme> {
    /**
     * 根据订单查询油站分配(订单)
     * @param orderId
     * @return
     */
    List<OrderOilDepotScheme> getOrderOilDepotSchemeByOrderId(Long orderId, Boolean isUpdate, LoginInfo user);
    /**
     * 根据订单ID删除油站分配方案
     * @param orderId
     * @return
     */
    String deleteOrderOilDepotSchem(Long orderId);


}
