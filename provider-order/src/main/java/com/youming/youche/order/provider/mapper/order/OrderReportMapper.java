package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.order.OrderReport;
import com.youming.youche.order.dto.VehiclesListDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper接口
 * </p>
 *
 * @author wuhao
 * @since 2022-03-29
 */
public interface OrderReportMapper extends BaseMapper<OrderReport> {

    List<Long> queryOrderOilEnRouteUse (Integer vehicleClass,
                                        List<Integer> vehicleClassOther,
                                        Integer orderState,
                                        Integer orderStateOther,
                                        Long userId,
                                        Long oilId);

    List<VehiclesListDto> selectOr(@Param("userId") Long userId, @Param("tenantId") Long tenantId,@Param("all")Boolean all);
}
