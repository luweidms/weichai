package com.youming.youche.order.api.monitor;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.monitor.MonitorOrderAgingArrive;

import java.util.List;

/**
 * 订单到达表
 *
 * @author hzx
 * @date 2022/3/11 16:47
 */
public interface IMonitorOrderAgingArriveService extends IBaseService<MonitorOrderAgingArrive> {

    /**
     * 多条件获取订单到达记录
     *
     * @param orderId     订单
     * @param plateNumber 车牌号
     * @param type        类型:1 预估迟到  2 实际迟到
     * @param tenantId    车队id
     * @return
     */
    List<MonitorOrderAgingArrive> getMonitorOrderAgingArrive(Long orderId, String plateNumber, Integer type, Long tenantId);

    /**
     * @param orderId 订单id
     * @param type    '类型:1 预估迟到  2 实际迟到 ',
     * @return
     */
    boolean deleteMonitorOrderAgingArrive(Long orderId, Integer type);

}
