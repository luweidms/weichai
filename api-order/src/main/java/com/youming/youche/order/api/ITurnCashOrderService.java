package com.youming.youche.order.api;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.TurnCashOrder;
import com.youming.youche.order.dto.order.TurnCashDto;

import java.util.List;

/**
* <p>
    *  服务类
    * </p>
* @author wuhao
* @since 2022-04-25
*/
public interface ITurnCashOrderService extends IBaseService<TurnCashOrder> {

    /**
     * 转现订单表
     * @param userId
     * @param orderId
     * @param batchId
     * @param balance
     * @param oilBalance
     * @param etcBalance
     * @param orderOilBalance
     * @param consumeOrderBalance
     * @param canTurnBalance
     * @param turnDiscount
     * @param vehicleAffiliation
     * @param turnMonth
     * @param oilCardNumber
     * @param turnType
     * @param tenantId
     * @param userType
     * @return
     */
    TurnCashOrder createTurnCashOrder(Long userId, Long orderId, Long batchId, Long balance, Long oilBalance, Long etcBalance, Long orderOilBalance,
                                  Long consumeOrderBalance, Long canTurnBalance, Long turnDiscount, String vehicleAffiliation, String turnMonth, String oilCardNumber, String turnType, Long tenantId,int userType);

    /**
     * App接口-ETC转现  28316
     */
    List<TurnCashDto> doQueryEtcCashover(Long orderId, Long tenantId);

    /**
     * App接口-油转现   28314
     */
    List<TurnCashDto> doQueryOilTransfer(Long orderId, Long tenantId);
}
