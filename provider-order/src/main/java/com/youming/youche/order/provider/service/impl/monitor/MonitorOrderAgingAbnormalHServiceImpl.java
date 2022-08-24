package com.youming.youche.order.provider.service.impl.monitor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.monitor.IMonitorOrderAgingAbnormalHService;
import com.youming.youche.order.domain.monitor.MonitorOrderAgingAbnormalH;
import com.youming.youche.order.provider.mapper.monitor.MonitorOrderAgingAbnormalHMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 异常数据历史表
 *
 * @author hzx
 * @date 2022/3/11 16:18
 */
@DubboService(version = "1.0.0")
public class MonitorOrderAgingAbnormalHServiceImpl extends BaseServiceImpl<MonitorOrderAgingAbnormalHMapper, MonitorOrderAgingAbnormalH> implements IMonitorOrderAgingAbnormalHService {

    @Resource
    MonitorOrderAgingAbnormalHMapper monitorOrderAgingAbnormalHMapper;

    @Override
    public List<MonitorOrderAgingAbnormalH> getMonitorOrderAgingAbnormalH(Long orderId, String plateNumber, Integer type, Integer lineType, Long tenantId) {

        LambdaQueryWrapper<MonitorOrderAgingAbnormalH> queryWrapper = new LambdaQueryWrapper<>();

        if (orderId != null && orderId > 0) queryWrapper.eq(MonitorOrderAgingAbnormalH::getOrderId, orderId);
        if (type != null && type > 0) queryWrapper.eq(MonitorOrderAgingAbnormalH::getType, type);
        if (tenantId != null && tenantId > 0) queryWrapper.eq(MonitorOrderAgingAbnormalH::getTenantId, tenantId);
        if (StringUtils.isNotBlank(plateNumber))
            queryWrapper.eq(MonitorOrderAgingAbnormalH::getPlateNumber, plateNumber);
        if (lineType != null && lineType > 0) queryWrapper.eq(MonitorOrderAgingAbnormalH::getLineType, lineType);

        List<MonitorOrderAgingAbnormalH> monitorOrderAgingAbnormalHS = getBaseMapper().selectList(queryWrapper);
        return monitorOrderAgingAbnormalHS;
    }

    @Override
    public List<Map<String, Object>> getValidityVehicle(Date expireDate, String plateNumber, int vehicleClass, Long tenantId) {
        if (vehicleClass == 6) {
            return monitorOrderAgingAbnormalHMapper.getValidityTrailerQuery("VEHICLE_VALIDITY_EXPIRED_TIME",
                    "vehicleValidityTime", tenantId, expireDate, 2, plateNumber);
        } else {
            return monitorOrderAgingAbnormalHMapper.getSearchVehicleQuery("VEHICLE_VALIDITY_TIME", "vehicleValidityTime",
                    tenantId, expireDate, 2, plateNumber, vehicleClass);
        }
    }

    @Override
    public List<Map<String, Object>> getInsuranceVehicle(Date expireDate, String plateNumber, int vehicleClass, Long tenantId) {
        if (vehicleClass == 6) {
            return monitorOrderAgingAbnormalHMapper.getSearchTrailerQuery("INSURANCE_EXPIRED_TIME",
                    "insuranceTime", tenantId, expireDate, 2, plateNumber);
        } else {
            return monitorOrderAgingAbnormalHMapper.getSearchVehicleQuery("INSURANCE_TIME_END",
                    "insuranceTime", tenantId, expireDate, 2, plateNumber, vehicleClass);
        }
    }

    @Override
    public List<Map<String, Object>> getBusiInsuranceVehicle(Date expireDate, String plateNumber, int vehicleClass, Long tenantId) {
        if (vehicleClass == 6) {
            return monitorOrderAgingAbnormalHMapper.getSearchTrailerQuery("BUSINESS_INSURANCE_EXPIRED_TIME",
                    "insuranceTime", tenantId, expireDate, 2, plateNumber);
        } else {
            return monitorOrderAgingAbnormalHMapper.getSearchVehicleQuery("BUSI_INSURANCE_TIME_END",
                    "insuranceTime", tenantId, expireDate, 2, plateNumber, vehicleClass);
        }
    }

    @Override
    public List<Map<String, Object>> getOtherInsuranceVehicle(Date expireDate, String plateNumber, int vehicleClass, Long tenantId) {
        if (vehicleClass == 6) {
            return monitorOrderAgingAbnormalHMapper.getSearchTrailerQuery("OTHER_INSURANCE_EXPIRED_TIME",
                    "otherInsuranceEndTime", tenantId, expireDate, 2, plateNumber);
        } else {
            return monitorOrderAgingAbnormalHMapper.getSearchVehicleQuery("OTHER_INSURANCE_TIME_END",
                    "otherInsuranceEndTime", tenantId, expireDate, 2, plateNumber, vehicleClass);
        }
    }

    @Override
    public List<Map<String, Object>> getOperateValidityVehicle(Date expireDate, String plateNumber, int vehicleClass, Long tenantId) {
        if (vehicleClass == 6) {
            return monitorOrderAgingAbnormalHMapper.getValidityTrailerQuery("OPERATE_VALIDITY_EXPIRED_TIME",
                    "operateValidityTime", tenantId, expireDate, 2, plateNumber);
        } else {
            return monitorOrderAgingAbnormalHMapper.getSearchVehicleQuery("OPERATE_VALIDITY_TIME",
                    "operateValidityTime", tenantId, expireDate, 2, plateNumber, vehicleClass);
        }
    }

}
