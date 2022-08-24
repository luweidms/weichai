package com.youming.youche.order.provider.mapper.order;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.order.OrderVehicleTimeNode;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * Mapper接口
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-25
 */
public interface OrderVehicleTimeNodeMapper extends BaseMapper<OrderVehicleTimeNode> {
    /**
     * 车辆时间节点查询
     * @param plateNumber
     * @param startDate
     * @param endDate
     * @param month
     * @param startOrderId
     * @param endOrderId
     * @param id 排除主键
     * @return
     * @throws Exception
     */
     List<OrderVehicleTimeNode> queryOrderVehicleTimeNode(@Param("plateNumber") String plateNumber,
                                                          @Param("startDate") String startDate,
                                                          @Param("endDate") String endDate,
                                                          @Param("month") String month,
                                                          @Param("startOrderId") Long startOrderId,
                                                          @Param("endOrderId") Long endOrderId,
                                                          @Param("id") Long id);


    List<OrderVehicleTimeNode> queryOrderVehicleTimeNodeByMonth(@Param("plateNumber") String plateNumber,
                                                                @Param("endDate") String endDate,
                                                                @Param("month") String month,
                                                                @Param("isSelelctNullEndDate") Boolean isSelelctNullEndDate);

    List<OrderVehicleTimeNode> queryOrderVehicleTimeNodeNew(@Param("plateNumber") String plateNumber, @Param("dependDate") Date dependDate,
                                                         @Param("month") String month, @Param("orderId") Long orderId);

}


