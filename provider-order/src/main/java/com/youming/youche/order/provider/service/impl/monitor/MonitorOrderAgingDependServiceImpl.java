package com.youming.youche.order.provider.service.impl.monitor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.order.api.monitor.IMonitorOrderAgingDependService;
import com.youming.youche.order.domain.monitor.MonitorOrderAgingDepend;
import com.youming.youche.order.provider.mapper.monitor.MonitorOrderAgingDependMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * 订单靠台历史表
 *
 * @author hzx
 * @date 2022/3/11 16:04
 */
@DubboService(version = "1.0.0")
public class MonitorOrderAgingDependServiceImpl extends BaseServiceImpl<MonitorOrderAgingDependMapper, MonitorOrderAgingDepend> implements IMonitorOrderAgingDependService {

    @Override
    public List<MonitorOrderAgingDepend> getMonitorOrderAgingDepend(Long orderId, String plateNumber, Integer type, Integer lineType, Long tenantId) {
        LambdaQueryWrapper<MonitorOrderAgingDepend> queryWrapper = new LambdaQueryWrapper<>();

        if (orderId != null && orderId > 0) queryWrapper.eq(MonitorOrderAgingDepend::getOrderId, orderId);
        if (type != null && type > 0) queryWrapper.eq(MonitorOrderAgingDepend::getType, type);
        if (tenantId != null && tenantId > 0) queryWrapper.eq(MonitorOrderAgingDepend::getTenantId, tenantId);
        if (StringUtils.isNotBlank(plateNumber)) queryWrapper.eq(MonitorOrderAgingDepend::getPlateNumber, plateNumber);
        if (lineType != null && lineType > 0) queryWrapper.eq(MonitorOrderAgingDepend::getLineType, lineType);

        queryWrapper.orderByDesc(MonitorOrderAgingDepend::getCreateTime);

        List<MonitorOrderAgingDepend> monitorOrderAgingDepends = getBaseMapper().selectList(queryWrapper);
        return monitorOrderAgingDepends;
    }

    @Override
    public boolean deleteMonitorOrderAgingDepend(Long orderId, Integer type, Integer lineType) {
        if (orderId != null && orderId > 0) {

        } else {
            throw new BusinessException("订单号不能为空！");
        }

        LambdaQueryWrapper<MonitorOrderAgingDepend> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MonitorOrderAgingDepend::getOrderId, orderId);
        if (type != null && type > 0) {
            queryWrapper.eq(MonitorOrderAgingDepend::getType, type);
        }
        if (lineType != null && lineType > 0) {
            queryWrapper.eq(MonitorOrderAgingDepend::getLineType, lineType);
        }

        return this.remove(queryWrapper);
    }

}
