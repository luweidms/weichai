package com.youming.youche.record.api.order;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.record.domain.order.OrderScheduler;
import com.youming.youche.record.dto.order.OrderSchedulerDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单调度表 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-20
 */
public interface IOrderSchedulerService extends IService<OrderScheduler> {


    /**
     * 查询司机订单信息
     * @param userId
     * @param tenantId
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    List<Map> queryDriverOrderInfo(Long userId, Long tenantId, String startDate, String endDate);



    /**
     * 查询司机订单
     * @return
     * @throws Exception
     */
    public boolean hasDriverOrder(Long userId);

    /**
     * 通过orderId查询order Info数据
     * @param orderId
     * @return
     * @throws Exception
     */
    OrderScheduler getOrderSchedulerByOrderId(Long orderId)throws Exception;

    /**
     * 订单查询订单调度信息
     *
     * @param orderId 订单id
     * @return
     */
    OrderScheduler getOrderScheduler(Long orderId);

    /***
     * @Description: 查询司机是否存在运力
     * @Author: luwei
     * @Date: 2022/5/10 11:45 下午
     * @Param userId:
      * @Param orderState:
     * @return: java.lang.Integer
     * @Version: 1.0
     **/
    Integer queryOrderdriverInfo(@Param("userId") Long userId, @Param("orderState") Integer orderState);

    /**
     * 获取etc 订单信息
     * @param vehicleCode
     * @param tenantId
     * @param fromOrderId
     * @param fromTenantId
     * @return
     */
    List<OrderSchedulerDto> queryOrderInfoByCar( Long vehicleCode, Long tenantId,
                                               Integer fromOrderId, Integer fromTenantId,Long orderId,String plateNumber);
}
