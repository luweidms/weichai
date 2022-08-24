package com.youming.youche.order.provider.service.impl.monitor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.monitor.IMonitorOrderAgingDependHService;
import com.youming.youche.order.domain.monitor.MonitorOrderAgingDependH;
import com.youming.youche.order.provider.mapper.monitor.MonitorOrderAgingDependHMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * 订单靠台历史表
 *
 * @author hzx
 * @date 2022/3/11 15:47
 */
@DubboService(version = "1.0.0")
public class MonitorOrderAgingDependHServiceImpl extends BaseServiceImpl<MonitorOrderAgingDependHMapper, MonitorOrderAgingDependH> implements IMonitorOrderAgingDependHService {

    @Override
    public List<MonitorOrderAgingDependH> getMonitorOrderAgingDependH(Long orderId, String plateNumber, Integer type, Integer lineType, Long tenantId) {

        LambdaQueryWrapper<MonitorOrderAgingDependH> queryWrapper = new LambdaQueryWrapper<>();

        if (orderId != null && orderId > 0) queryWrapper.eq(MonitorOrderAgingDependH::getOrderId, orderId);
        if (type != null && type > 0) queryWrapper.eq(MonitorOrderAgingDependH::getType, type);
        if (tenantId != null && tenantId > 0) queryWrapper.eq(MonitorOrderAgingDependH::getTenantId, tenantId);
        if (StringUtils.isNotBlank(plateNumber)) queryWrapper.eq(MonitorOrderAgingDependH::getPlateNumber, plateNumber);
        if (lineType != null && lineType > 0) queryWrapper.eq(MonitorOrderAgingDependH::getLineType, lineType);

        queryWrapper.orderByDesc(MonitorOrderAgingDependH::getCreateTime);

        List<MonitorOrderAgingDependH> monitorOrderAgingDependHS = getBaseMapper().selectList(queryWrapper);
        return monitorOrderAgingDependHS;
    }

}
