package com.youming.youche.finance.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.domain.order.OrderProblemInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 订单异常登记表
 *
 * @author hzx
 * @date 2022/2/10 16:59
 */
public interface OrderProblemInfoMapper extends BaseMapper<OrderProblemInfo> {

    List<Map<String, Object>> checkOrderExceptionCreateBill(@Param("orderIds") String orderIds);

}
