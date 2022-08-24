package com.youming.youche.order.api.order;


import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderVehicleTimeNode;
import com.youming.youche.order.dto.OrderCostRetrographyDto;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * 服务类
 * </p>
 *
<<<<<<< HEAD
 * @author CaoYaJie
=======
 * @author chenzhe
>>>>>>> origin/feature/order
 * @since 2022-03-25
 */
public interface IOrderVehicleTimeNodeService extends IBaseService<OrderVehicleTimeNode> {

    /**
     * 添加车辆时间节点
     * @param startOrderId
     * @param startDate 开始时间
     * @param plateNumber
     * @param tenantId
     */

    void addVehicleTimeNode(Long startOrderId, LocalDateTime startDate, String plateNumber, Long tenantId);


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
    List<OrderVehicleTimeNode> queryOrderVehicleTimeNode(String plateNumber,String startDate, String endDate,
                                                         String month, Long startOrderId, Long endOrderId, Long id);



    /**
     * 查询指定月份车辆成本时间
     * @param plateNumber 车牌号
     * @param endDate 结束时间
     * @param month 月份:yyyy-mm
     * @param isSelelctNullEndDate 是否查询空值EndDate
     * @return
     * @throws Exception
     */
    List<OrderVehicleTimeNode> queryOrderVehicleTimeNodeByMonth(String plateNumber,String endDate,String month,
                                                                Boolean isSelelctNullEndDate);

   // void addVehicleTimeNode(Long orderId, LocalDateTime dependTime, String plateNumber, Long tenantId);

    /**
     * 删除车辆时间节点
     */
    void delVehicleTimeNode(Long orderId, Date startDate, String plateNumber, Long tenantId);

    /*
     * 查询订单所处节点
     */
    OrderVehicleTimeNode queryOrderVehicleTimeNode(String plateNumber,Date dependDate,String month,Long orderId);

    /**
     * 根据车辆时间节点写入订单成本
     * @param vehicleTimeNodes
     * @param isSelect 是否查询
     */
    OrderCostRetrographyDto setOrderCostFeeByTimeNode (List<OrderVehicleTimeNode> vehicleTimeNodes, boolean isSelect, String accessToken);

    /**
     * 截断时间节点
     * @param orderId
     * @param plateNumber
     * @throws Exception
     */
    void truncationVehicleTimeNode(Long orderId,String plateNumber);

}
