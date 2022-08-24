package com.youming.youche.record.api.vehicle;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.record.domain.vehicle.VehicleObjectLine;
import com.youming.youche.record.dto.VehicleObjectLineDto;

import java.util.List;

/**
 * <p>
 * 车辆心愿线路表 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
public interface IVehicleObjectLineService extends IService<VehicleObjectLine> {

    /**
     * 方法实现说明  根据车队车牌删除车辆心愿线路*
     * @author      terry
     * @param vehicleCode
    * @param tenantId
     * @return      void
     * @exception
     * @date        2022/3/14 19:18
     */
    void remove(Long vehicleCode, Long tenantId);

    /**
     * 根据车牌号获取心愿路线
     *
     * @param vehicleCode 车牌编号
     * @param tenantId    车队id
     */
    List<VehicleObjectLineDto> getVehicleObjectLineForApp(long vehicleCode, Long tenantId);

}
