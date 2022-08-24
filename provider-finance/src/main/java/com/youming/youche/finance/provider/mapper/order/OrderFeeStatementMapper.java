package com.youming.youche.finance.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.domain.order.OrderFeeStatement;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单账单信息表
 *
 * @author hzx
 * @date 2022/2/9 15:54
 */
public interface OrderFeeStatementMapper extends BaseMapper<OrderFeeStatement> {

    List<OrderFeeStatement> queryFeeByBillNumber(@Param("billNumber") String billNumber);

    int updateFeeByHId(@Param("orderFeeStatement") OrderFeeStatement orderFeeStatement);

    List<Long> getOrderIdsByBill(@Param("billNumbers") String billNumbers);

    // 根据账单号s，将不是已核销1和部分核销2的记录该为开票状态3
    int updateFeeByFields(@Param("billNumbers") String billNumbers);

    // 回退 新建 >> 已废弃
    int updateGoBackStatusByBillNumber0(@Param("billNumber") String billNumber);

    // 回退 已开票 >> 新建
    int updateGoBackStatusByBillNumber1(@Param("billNumber") String billNumber);

    // 回退 核销  >> 已开票
    int updateGoBackStatusByBillNumber2(@Param("billNumber") String billNumber);

    Long getOrderFeeStatementByBillNumber(@Param("billNumber") String billNumber);

    List<OrderFeeStatement> getFeeStatementByOrderIds(@Param("orderIds") String orderIds);

    List<Map<String, Object>> checkOrderCreateBill(@Param("orderIds") String orderIds,
                                                   @Param("tenantId") Long tenantId,
                                                   @Param("billNumber") String billNumber);

    // 账单增加订单
    int updateBillNumberByOrderIds(@Param("billNumber") String billNumber, @Param("orderIds") String orderIds);

    // 查看账单的所有订单（校验不可移除所有的单）
    int getOrderNumBuBillNumber(@Param("billNumber") String billNumber);

    // 账单减少订单
    int updateBillNumberByOrderIdsAndBillNumber(@Param("billNumber") String billNumber, @Param("orderIds") String orderIds);

    // 账单减少订单
    int queryBillNumberByOrderIdsAndBillNumber(@Param("billNumber") String billNumber, @Param("orderIds") String orderIds);

    // 订单 -- 对账调整
    int updateOrderDiffByOrderId(@Param("confirmAmount") Long confirmAmount,
                                 @Param("diffFeeSum") Long diffFeeSum,
                                 @Param("orderId") Long orderId,
                                 @Param("userId") Long userId,
                                 @Param("updateTime") LocalDateTime updateTime);
}
