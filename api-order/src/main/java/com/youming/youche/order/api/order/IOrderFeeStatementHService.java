package com.youming.youche.order.api.order;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.order.domain.order.OrderFeeStatementH;

/**
 * <p>
 * 订单账单信息历史表 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-20
 */
public interface IOrderFeeStatementHService extends IService<OrderFeeStatementH> {


    /**
     * 获取订单账单信息历史记录
     * @param orderId
     * @return
     */
    OrderFeeStatementH getOrderFeeStatementH(Long orderId);

    /**
     * 保存订单账单信息历史记录
     *
     * @param ofsh
     * @param accessToken
     */
    void saveOrderFeeStatementH(OrderFeeStatementH ofsh, String accessToken);

}
