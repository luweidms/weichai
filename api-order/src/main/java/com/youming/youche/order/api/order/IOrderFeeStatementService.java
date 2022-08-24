package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.order.OrderFeeStatement;

import java.time.LocalDateTime;

/**
 * <p>
 * 订单账单信息表 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-20
 */
public interface IOrderFeeStatementService extends IBaseService<OrderFeeStatement> {
    /**
     * 通过账单Id查询账单相关信息
     * @param orderId
     * @return
     * @throws Exception
     */
    OrderFeeStatement getOrderFeeStatementById(Long orderId);


    /**
     * 保存订单账单信息
     * @param ofs
     * @throws Exception
     */
    void saveOrderFeeStatement(OrderFeeStatement ofs, LoginInfo user);

    /**
     * 更新订单账单的结算到期日
     * @param orderId
     * @param receiveSettleDueDate 结算到期日-应收
     * @throws Exception
     */
     void updateReceiveSettleDueDate(Long orderId, LocalDateTime receiveSettleDueDate);


    /**
     * 根据订单号获取账单相关信息
     * @param orderId
     * @return
     */
    OrderFeeStatement getOrderFeeStatement(Long orderId);


    /**
     * 更新订单账单的结算到期日
     * @param orderId
     * @param paySettleDueDate 结算到期日-应付
     * @throws Exception
     */
     void updatePaySettleDueDate(Long orderId,LocalDateTime paySettleDueDate);

    /**
     * 将订单账单信息移到历史表
     */
    void moveOrderFeeStatementToHistory(Long orderId,String accessToken);


    /**
     * 支付预付款、尾款到期，修改  SOURCE_CHECK_AMOUNT、GET_AMOUNT 字段
     *
     * @param orderId 订单ID
     * @param amount  金额（单位：分）
     */
    void checkOrderAmountByProcess(Long orderId, Long amount, String accessToken);

    /**
     * 支付预付款、尾款到期，修改  SOURCE_CHECK_AMOUNT、GET_AMOUNT 字段
     * @param orderId 订单ID
     * @param amount 金额（单位：分）
     */
    void checkOrderAmountByProcess(Long orderId,Long amount);
}
