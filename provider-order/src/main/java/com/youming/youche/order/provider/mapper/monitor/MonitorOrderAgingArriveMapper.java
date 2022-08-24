package com.youming.youche.order.provider.mapper.monitor;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.monitor.MonitorOrderAgingArrive;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 订单到达表
 *
 * @author hzx
 * @date 2022/3/9 14:57
 */
public interface MonitorOrderAgingArriveMapper extends BaseMapper<MonitorOrderAgingArrive> {

    List<MonitorOrderAgingArrive> queryAgingArriveList(@Param("isOrderHis") String isOrderHis, @Param("isHis") String isHis,
                                                       @Param("orderId") Long orderId, @Param("type") Integer type,
                                                       @Param("tenantId") Long tenantId, @Param("sourceRegion") Integer sourceRegion,
                                                       @Param("desRegion") Integer desRegion, @Param("orgId") Long orgId,
                                                       @Param("plateNumber") String plateNumber);

    /**
     * 迟到
     * 车辆到达时间 晚于 靠台时间+到达时限  的车辆,      订单已完成/撤销,  车辆迟到也跟随消失
     */
    List<Map> queryAgingArriveListNew(@Param("tenantId") Long tenantId, @Param("plateNumber") String plateNumber,
                                      @Param("orgId") Long orgId,
                                      @Param("sourceRegion") Integer sourceRegion, @Param("desRegion") Integer desRegion);


}
