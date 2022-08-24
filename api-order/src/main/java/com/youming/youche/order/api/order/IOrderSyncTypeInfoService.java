package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderSyncTypeInfo;

import java.util.List;

/**
 * <p>
 * 订单同步表 服务类
 * </p>
 *
 * @author hzx
 * @since 2022-03-25
 */
public interface IOrderSyncTypeInfoService extends IBaseService<OrderSyncTypeInfo> {

    /**
     * 保存订单同步类型
     *
     * @param orderId  订单号
     * @param syncType 同步类型
     * @param billType 票据类型
     */
    void saveOrderSyncTypeInfo(Long orderId, Integer syncType, Integer billType, String accessToken);

    /**
     * 查询订单同步信息
     */
    List<OrderSyncTypeInfo> queryOrderSyncTypeInfoList(Long orderId, Integer syncType, Integer billType,
                                                       Integer taskDisposeSts);

}
