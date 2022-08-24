package com.youming.youche.record.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.order.OrderInfo;
import org.apache.ibatis.annotations.Param;

/**
 * @Date:2021/12/22
 */
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    Long queryVehicleOrderInfoIn(@Param("plateNumber")String plateNumber);
}
