package com.youming.youche.order.api;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.TurnCashLog;

/**
* <p>
    *  服务类
    * </p>
* @author wuhao
* @since 2022-04-25
*/
    public interface ITurnCashLogService extends IBaseService<TurnCashLog> {

    /**
     * 保存油和etc转现记录
     * @param id
     * @param soNbr
     * @param balance
     * @param marginBalance
     * @param oilBalance
     * @param etcBalance
     * @param orderOil
     * @param orderEtc
     * @param turnBalance
     * @param turnDiscountDouble
     * @param turnType
     * @param turnMonth
     * @param vehicleAffiliation
     * @param tenantId
     * @param userType
     * @param accessToken
     * @return
     */
    TurnCashLog createTurnCashLog(Long id, Long soNbr, Long balance, Long marginBalance,
                                  Long oilBalance, Long etcBalance, Long orderOil, Long orderEtc,
                                  Long turnBalance, Long turnDiscountDouble, String turnType, String turnMonth,
                                  String vehicleAffiliation, Long tenantId, Integer userType,String accessToken);

}
