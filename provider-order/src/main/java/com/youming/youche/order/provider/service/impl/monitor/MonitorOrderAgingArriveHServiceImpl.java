package com.youming.youche.order.provider.service.impl.monitor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.monitor.IMonitorOrderAgingArriveHService;
import com.youming.youche.order.domain.monitor.MonitorOrderAgingArriveH;
import com.youming.youche.order.provider.mapper.monitor.MonitorOrderAgingArriveHMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * 订单到达历史表
 *
 * @author hzx
 * @date 2022/3/11 16:32
 */
@DubboService(version = "1.0.0")
public class MonitorOrderAgingArriveHServiceImpl extends BaseServiceImpl<MonitorOrderAgingArriveHMapper, MonitorOrderAgingArriveH> implements IMonitorOrderAgingArriveHService {

    @Override
    public List<MonitorOrderAgingArriveH> getMonitorOrderAgingArriveH(Long orderId, String plateNumber, Integer type, Long tenantId) {
        LambdaQueryWrapper<MonitorOrderAgingArriveH> queryWrapper = new LambdaQueryWrapper<>();

        if (orderId != null && orderId > 0) queryWrapper.eq(MonitorOrderAgingArriveH::getOrderId, orderId);
        if (type != null && type > 0) queryWrapper.eq(MonitorOrderAgingArriveH::getType, type);
        if (tenantId != null && tenantId > 0) queryWrapper.eq(MonitorOrderAgingArriveH::getTenantId, tenantId);
        if (StringUtils.isNotBlank(plateNumber)) queryWrapper.eq(MonitorOrderAgingArriveH::getPlateNumber, plateNumber);

        List<MonitorOrderAgingArriveH> monitorOrderAgingAbnormalHS = getBaseMapper().selectList(queryWrapper);
        return monitorOrderAgingAbnormalHS;
    }

}
