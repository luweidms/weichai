package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.OrderRetrographyCostInfo;

/**
 * <p>
 * 订单校准里程表 服务类
 * </p>
 *
 * @author chenzhe
 * @since 2022-03-20
 */
public interface IOrderRetrographyCostInfoService extends IBaseService<OrderRetrographyCostInfo> {

    /**
     * 获取校准载重、空载后的订单成本信息
     */
    OrderRetrographyCostInfo queryOrderRetrographyCostInfoByCheck(Long orderId);

    /**
     * 获取订单反写成本信息
     */
    OrderRetrographyCostInfo queryOrderRetrographyCostInfo(Long orderId);

}
