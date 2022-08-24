package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderStateTrackOper;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
public interface IOrderStateTrackOperService extends IBaseService<OrderStateTrackOper> {
    /**
     * 操作记录保存
     * @param orderId 订单Id
     * @param vehicleCode 车辆ID
     * @param opType 操作类型
     * @throws Exception
     */
    void saveOrUpdate(Long orderId, Long vehicleCode, Integer opType);


    /**
     * 查询操作记录
     * @param orderId 订单号
     * @return
     * @throws Exception
     */
    OrderStateTrackOper getOrderStateTrackOperByOrderId(Long orderId);
}
