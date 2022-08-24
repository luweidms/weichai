package com.youming.youche.order.api.monitor;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.monitor.MonitorOrderAgingAbnormalH;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 异常数据历史表
 *
 * @author hzx
 * @date 2022/3/11 16:19
 */
public interface IMonitorOrderAgingAbnormalHService extends IBaseService<MonitorOrderAgingAbnormalH> {
    /**
     * 查询历史异常数据
     * @param orderId
     * @param plateNumber
     * @param type
     * @param lineType
     * @param tenantId
     * @return
     */
    List<MonitorOrderAgingAbnormalH> getMonitorOrderAgingAbnormalH(Long orderId, String plateNumber, Integer type, Integer lineType, Long tenantId);

    /**
     * 获取需要年审的车辆
     */
    List<Map<String, Object>> getValidityVehicle(Date expireDate, String plateNumber, int vehicleClass, Long tenantId);

    /**
     * 获取保险快到期的车辆
     */
    List<Map<String, Object>> getInsuranceVehicle(Date expireDate, String plateNumber, int vehicleClass, Long tenantId);

    /**
     * 获取商业险快到期的车辆
     */
    List<Map<String, Object>> getBusiInsuranceVehicle(Date expireDate, String plateNumber, int vehicleClass, Long tenantId);

    /**
     * 获取其他险快到期的车辆
     */
    List<Map<String, Object>> getOtherInsuranceVehicle(Date expireDate, String plateNumber, int vehicleClass, Long tenantId);

    /**
     * 获取营运证快到期的车辆
     */
    List<Map<String, Object>> getOperateValidityVehicle(Date expireDate, String plateNumber, int vehicleClass, Long tenantId);

}
