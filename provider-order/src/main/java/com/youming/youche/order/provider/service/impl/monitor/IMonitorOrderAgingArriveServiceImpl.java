package com.youming.youche.order.provider.service.impl.monitor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.order.api.monitor.IMonitorOrderAgingArriveService;
import com.youming.youche.order.domain.monitor.MonitorOrderAgingArrive;
import com.youming.youche.order.provider.mapper.monitor.MonitorOrderAgingArriveMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * 订单到达表
 *
 * @author hzx
 * @date 2022/3/11 16:47
 */
@DubboService(version = "1.0.0")
public class IMonitorOrderAgingArriveServiceImpl extends BaseServiceImpl<MonitorOrderAgingArriveMapper, MonitorOrderAgingArrive> implements IMonitorOrderAgingArriveService {

    @Override
    public List<MonitorOrderAgingArrive> getMonitorOrderAgingArrive(Long orderId, String plateNumber, Integer type, Long tenantId) {
        LambdaQueryWrapper<MonitorOrderAgingArrive> queryWrapper = new LambdaQueryWrapper<>();

        if (orderId != null && orderId > 0) queryWrapper.eq(MonitorOrderAgingArrive::getOrderId, orderId);
        if (type != null && type > 0) queryWrapper.eq(MonitorOrderAgingArrive::getType, type);
        if (tenantId != null && tenantId > 0) queryWrapper.eq(MonitorOrderAgingArrive::getTenantId, tenantId);
        if (StringUtils.isNotBlank(plateNumber)) queryWrapper.eq(MonitorOrderAgingArrive::getPlateNumber, plateNumber);

        List<MonitorOrderAgingArrive> monitorOrderAgingArrives = getBaseMapper().selectList(queryWrapper);

        return monitorOrderAgingArrives;
    }

    @Override
    public boolean deleteMonitorOrderAgingArrive(Long orderId, Integer type) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单号不能为空！");
        }
        LambdaQueryWrapper<MonitorOrderAgingArrive> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MonitorOrderAgingArrive::getOrderId, orderId);
        if (type != null && type > 0) {
            queryWrapper.eq(MonitorOrderAgingArrive::getType, type);
        }

        return this.remove(queryWrapper);
    }

}
