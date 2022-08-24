package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.order.VehicleReturnInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper接口
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-25
 */
public interface VehicleReturnInfoMapper extends BaseMapper<VehicleReturnInfo> {

   List<String> queryRecentArriveDate(@Param("plateNumber") String plateNumber,
                                      @Param("orderId") Long orderId);



}
