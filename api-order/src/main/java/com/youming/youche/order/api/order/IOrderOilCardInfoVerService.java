package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderOilCardInfoVer;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-21
 */
public interface IOrderOilCardInfoVerService extends IBaseService<OrderOilCardInfoVer> {


    /**
     * 释放油卡抵押余额
     * @param orderId
     * @throws Exception
     */
    void releaseOilCardBalance(long orderId,Long tenantId);

    /**
     * 根据订单号查询订单油卡
     * @param orderId 订单ID
     * @param oilCardNum 油卡号
     * @param updateState 修改状态
     * @return
     * @throws Exception
     */
    List<OrderOilCardInfoVer> queryOrderOilCardInfoVerByOrderId(Long orderId, String oilCardNum, Integer updateState);


}
