package com.youming.youche.finance.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.domain.order.OrderFeeH;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单费用历史表
 *
 * @author hzx
 * @date 2022/2/16 16:14
 */
public interface OrderFeeHMapper extends BaseMapper<OrderFeeH> {

    List<OrderFeeH> getOrderFeeHByOrderId(@Param("orderId") Long orderId);

}
