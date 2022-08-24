package com.youming.youche.record.provider.mapper.order;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.order.OrderScheduler;
import com.youming.youche.record.dto.order.OrderSchedulerDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单调度表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2021-11-20
 */
public interface OrderSchedulerMapper extends BaseMapper<OrderScheduler> {

    List<Map> queryOrderInfo(@Param("userId") Long userId, @Param("tenantId") Long tenantId,
                             @Param("startDate") String startDate, @Param("endDate") String endDate,
                             @Param("orderState") Integer orderState);


    List<Map> queryOrderInfoH(@Param("userId") Long userId, @Param("tenantId") Long tenantId,
                              @Param("startDate") String startDate, @Param("endDate") String endDate,
                              @Param("orderState") Integer orderState);

    Integer queryOrderdriverInfo(@Param("userId") Long userId,@Param("orderState") Integer orderState);

    /**
     * 查询指定违章时间司机
     *
     * @param plateNumber
     * @param violationDate
     * @return carDriverPhone 司机手机号
     * carDriverId 司机ID
     * orderId 订单号
     * arriveDate 到达时间
     * @throws Exception
     */
    List<Map> queryDriverViolationInfo(@Param("plateNumber") String plateNumber,
                                       @Param("violationDate") String violationDate,
                                       @Param("estStartTime") Double estStartTime,
                                       @Param("isGt") String isGt);

    @Select("SELECT * from   order_scheduler where order_id =#{orderId}")
    OrderScheduler getOrderScheduler(Long orderId);


    /**
     * 根据车辆找订单(ETC扣费)
     * @param vehicleCode   车辆ID
     * @param tenantId    租户ID
     * @param fromOrderId  来源订单ID
     * @param fromTenantId  来源租户ID
     * @return
     */
    List<OrderSchedulerDto> queryOrderInfoByCar(@Param("vehicleCode") Long vehicleCode, @Param("tenantId")Long tenantId,
                                                @Param("fromOrderId") Integer fromOrderId,
                                                @Param("fromTenantId")Integer fromTenantId,
                                                @Param("orderState")Integer orderState,
                                                @Param("orderId")Long orderId,
                                                @Param("plateNumber")String plateNumber);
    /**
     * 根据车辆找订单(ETC扣费)
     * @param vehicleCode   车辆ID
     * @param tenantId    租户ID
     * @param fromOrderId  来源订单ID
     * @param fromTenantId  来源租户ID
     * @return
     */
    List<OrderSchedulerDto> queryOrderInfoByCarH(@Param("vehicleCode") Long vehicleCode,
                                                 @Param("tenantId")Long tenantId,
                                                @Param("fromOrderId") Integer fromOrderId,
                                                 @Param("fromTenantId")Integer fromTenantId,
                                                 @Param("orderState")Integer orderState,
                                                 @Param("orderId")Long orderId,
                                                 @Param("plateNumber")String plateNumber);

}
