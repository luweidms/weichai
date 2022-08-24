package com.youming.youche.order.api.monitor;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.monitor.MonitorOrderAgingDependH;

import java.util.List;

/**
 * 订单靠台历史表
 *
 * @author hzx
 * @date 2022/3/11 15:45
 */
public interface IMonitorOrderAgingDependHService extends IBaseService<MonitorOrderAgingDependH> {

    /**
     * @param orderId     订单id
     * @param plateNumber 车牌号
     * @param type        类型:1 预估迟到  2 实际迟到
     * @param lineType    类型:0 为起始地 >0 为经停点(经停点序号)
     * @param tenantId    车队id
     */
    List<MonitorOrderAgingDependH> getMonitorOrderAgingDependH(Long orderId, String plateNumber, Integer type, Integer lineType, Long tenantId);

}
