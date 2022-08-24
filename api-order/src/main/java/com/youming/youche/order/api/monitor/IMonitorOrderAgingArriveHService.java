package com.youming.youche.order.api.monitor;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.monitor.MonitorOrderAgingArriveH;

import java.util.List;

/**
 * 订单到达历史表
 *
 * @author hzx
 * @date 2022/3/11 16:31
 */
public interface IMonitorOrderAgingArriveHService extends IBaseService<MonitorOrderAgingArriveH> {

    /**
     * 订单到达历史表
     *
     * @param orderId     订单号
     * @param plateNumber 车牌号
     * @param type        类型:1    预估迟到  2 实际迟到
     * @param tenantId    车队id
     * @return
     */
    List<MonitorOrderAgingArriveH> getMonitorOrderAgingArriveH(Long orderId, String plateNumber, Integer type, Long tenantId);

}
