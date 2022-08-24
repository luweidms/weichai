package com.youming.youche.order.provider.mapper.order;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Mapper接口
 *
 * @author qyf
 * @since 2022-03-19
 */
public interface OrderSchedulerHMapper extends BaseMapper<OrderSchedulerH> {

    /**
     * 根据车牌号查询最近的已完成订单的调度信息
     * 要排除撤单的情况
     *
     * @param plateNumber 车牌号码
     * @param dependTime  靠台时间
     * @param orderId     排除订单号
     */
    List<OrderSchedulerH> getPreOrderSchedulerHByPlateNumber(@Param("plateNumber") String plateNumber, @Param("dependTime") String dependTime,
                                                             @Param("tenandId") Long tenandId, @Param("orderId") Long orderId);

    /**
     * 根据挂车车牌查找最后一单
     */
    List<OrderSchedulerH> getPreOrderSchedulerHByTrailerPlate(@Param("trailerPlate") String trailerPlate, @Param("tenantId") Long tenantId);

    /**
     * 根据车牌号查询最近的已完成订单的调度信息
     * 要排除撤单的情况
     *
     * @param plateNumber 车牌号码
     * @param dependTime  靠台时间
     * @param orderId     排除订单号
     */
    List<OrderSchedulerH> getPreOrderSchedulerHByPlateNumber14(@Param("plateNumber") String plateNumber, @Param("dependTime") String dependTime,
                                                             @Param("tenandId") Long tenandId, @Param("orderId") Long orderId);

    /**
     * 根据车牌号查询靠台时间下一单订单的调度信息
     *
     * @param plateNumber 车牌号码
     * @param dependTime 靠台时间
     * @param orderId 排除订单号
     */
    OrderSchedulerH getNextOrderSchedulerHByPlateNumber(@Param("plateNumber") String plateNumber, @Param("dependTime") Date dependTime,
                                                        @Param("tenantId")  Long tenantId,@Param("orderId")  Long orderId) ;

}
