package com.youming.youche.finance.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.domain.order.OrderReceipt;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 订单-回单
 *
 * @author hzx
 * @date 2022/2/8 11:21
 */
public interface OrderReceiptMapper extends BaseMapper<OrderReceipt> {

    /**
     * 查询订单对应客户单号
     */
    List<Map<String, Object>> getCustomerIdByOrderIds(@Param("orderIds") String orderIds);

    /**
     * 查找订单回单
     *
     * @param orderId
     * @param flowId
     */
    List<OrderReceipt> findOrderRecipts(@Param("orderId") Long orderId, @Param("flowId") String flowId);
}
