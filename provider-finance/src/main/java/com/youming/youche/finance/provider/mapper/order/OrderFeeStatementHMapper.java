package com.youming.youche.finance.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.domain.order.OrderFeeStatementH;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单账单信息历史表
 *
 * @author hzx
 * @date 2022/2/9 15:28
 */
public interface OrderFeeStatementHMapper extends BaseMapper<OrderFeeStatementH> {

    List<OrderFeeStatementH> queryFeeHByBillNumber(@Param("billNumber") String billNumber);

    int updateFeeHByHId(@Param("orderFeeStatementH") OrderFeeStatementH orderFeeStatementH);

    // 根据账单号s，将不是已核销1和部分核销2的记录该为开票状态3
    int updateFeeHByFields(@Param("billNumbers") String billNumbers);

    // 回退 新建 >> 已废弃
    int updateGoBackStatusHByBillNumber0(@Param("billNumber") String billNumber);

    // 回退 已开票 >> 新建
    int updateGoBackStatusHByBillNumber1(@Param("billNumber") String billNumber);

    // 回退 核销  >> 已开票
    int updateGoBackStatusHByBillNumber2(@Param("billNumber") String billNumber);

    List<OrderFeeStatementH> getFeeStatementHByOrderIds(@Param("orderIds") String orderIds);

    List<Map<String, Object>> checkOrderCreateBill(@Param("orderIds") String orderIds,
                                                   @Param("tenantId") Long tenantId);

    // 增加账单
    int updateBillNumberByOrderIds(@Param("billNumber") String billNumber, @Param("orderIds") String orderIds);

    // 账单减少订单
    int updateBillNumberByOrderIdsAndBillNumber(@Param("billNumber") String billNumber, @Param("orderIds") String orderIds);

    // 账单减少订单 -- 查询
    Integer queryBillNumberByOrderIdsAndBillNumber(@Param("billNumber") String billNumber, @Param("orderIds") String orderIds);

    // 订单 -- 对账调整
    int updateOrderDiffByOrderId(@Param("confirmAmount") Long confirmAmount,
                                 @Param("diffFeeSum") Long diffFeeSum,
                                 @Param("orderId") Long orderId,
                                 @Param("userId") Long userId,
                                 @Param("updateTime") LocalDateTime updateTime);

}
