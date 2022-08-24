package com.youming.youche.record.api.vehicle;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.record.domain.vehicle.VehicleDataInfoVer;

import java.util.List;

/**
 * <p>
 * 车辆版本表 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
public interface IVehicleDataInfoVerService extends IService<VehicleDataInfoVer> {

    /**
     * 获取车辆历史信息
     *
     * @param plateNumber 车牌号
     * @param tenantId    车队id
     * @return
     */
    VehicleDataInfoVer getVehicleDataInfoVer(String plateNumber, Long tenantId);

    /**
     * 车辆编号查询车辆历史信息
     *
     * @param vehicleCode 车辆编号
     * @return
     */
    VehicleDataInfoVer getVehicleDataInfoVer(Long vehicleCode);

    /**
     * 创建VehicleDataInfoVer历史数据
     *
     * @param vehicleCode 车辆编号
     * @return
     * @throws Exception
     */
    VehicleDataInfoVer doAddVehicleDataInfoVerHis(Long vehicleCode) throws BusinessException;

    /**
     * 获取车辆历史信息
     *
     * @param vehicleCode 车辆编号
     * @param tenantId    车队id
     * @return
     */
    List<VehicleDataInfoVer> getVehicleObjectVer(Long vehicleCode, Long tenantId);

}
