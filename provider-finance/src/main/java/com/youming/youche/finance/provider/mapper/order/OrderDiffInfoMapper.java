package com.youming.youche.finance.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.domain.order.OrderDiffInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 订单差异明细
 *
 * @author hzx
 * @date 2022/2/8 11:11
 */
public interface OrderDiffInfoMapper extends BaseMapper<OrderDiffInfo> {

    /**
     * 根据orderId查询订单差异列表
     */
    List<OrderDiffInfo> getOrderDiffList(@Param("orderId") String orderId);

    /**
     * 查询订单对账金额
     */
    List<Map<String, Object>> getDiffFeeByOrderIds(@Param("orderIds") String orderIds);

    /**
     * 删除差异金额
     */
    int deleteDiffInfoByOrderId(@Param("orderId") Long orderId);

}
