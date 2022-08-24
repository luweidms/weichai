package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderSchedulerH;

import java.time.LocalDateTime;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-20
 */
public interface IOrderSchedulerHService extends IBaseService<OrderSchedulerH> {

    OrderSchedulerH selectByOrderId(Long orderId);

    /**
     * 根据车牌号查询最近的已完成订单的调度信息
     * 要排除撤单的情况
     *
     * @param plateNumber 车牌号码
     * @param dependTime 靠台时间
     * @param orderId 排除订单号
     * @return
     */
     OrderSchedulerH getPreOrderSchedulerHByPlateNumber(String plateNumber, LocalDateTime dependTime,
                                                              Long tenandId, Long orderId);



    /**
     * 根据订单号获取订单历史调度信息
     * @param orderId
     * @return
     * @throws Exception
     */
     OrderSchedulerH getOrderSchedulerH(Long orderId);



    /**
     * 查询司机id最近的已完成订单的调度信息
     * 要排除撤单的情况
     *
     * @param userId 主驾驶的用户id
     * @param dependTime 靠台时间
     * @param type 1 表示主驾驶 2 表示副驾驶
     * @param orderId 排除的的订单id
     * @param isLastOrder 是否最后单
     * @return
     */
     OrderSchedulerH getPreOrderSchedulerHByUserId(Long userId,LocalDateTime dependTime,
                                                         Long tenandId,Long orderId,
                                                         Boolean isLastOrder);
/*
     * @param dependTime  靠台时间
     * @param orderId     排除订单号
     *//*

    OrderSchedulerH getPreOrderSchedulerHByPlateNumber(String plateNumber, Date dependTime, Long tenandId, Long orderId);
*/

    /**
     * 根据挂车车牌查找最后一单
     */
    OrderSchedulerH getPreOrderSchedulerHByTrailerPlate(String trailerPlate, Long tenantId);




    /**
     * 通过车牌查找最后一单订单
     * @param plateNumber
     * @return
     */
    OrderSchedulerH getOrderSchedulerByPlateNumber(String plateNumber);
}
