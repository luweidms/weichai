package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.PayoutOrder;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
public interface IPayoutOrderService extends IBaseService<PayoutOrder> {

    /**
     * 写入payout_order
     * @param userId
     * @param amount
     * @param amountType
     * @param batchId
     * @param orderId
     * @param tenantId
     * @param vehicleAffiliation
     * @return
     */
    PayoutOrder createPayoutOrder(Long userId, Long amount, Integer amountType, Long batchId ,
                                  Long orderId, Long tenantId,String vehicleAffiliation);

    /**
     * 修改payout_order记录
     * @param batchId
     * @param flowId
     */
    void updPayoutOrder(Long batchId, Long flowId);

    /**
     * 根据流水号查询付款订单记录
     *
     * @param batchIds
     * @return
     */
    List<PayoutOrder> getPayoutOrderList(List<Long> batchIds);

}
