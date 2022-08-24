package com.youming.youche.record.api.vehicle;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.record.domain.vehicle.VehicleObjectLineVer;
import com.youming.youche.record.dto.VehicleObjectLineDto;

import java.util.List;

/**
 * @Date:2021/12/28
 */
public interface IVehicleObjectLineVerService extends IService<VehicleObjectLineVer> {

    /**
     * 多条件获取心愿路线
     *
     * @param vehicleCode
     * @param hisId
     * @param verState
     * @param flg
     * @return
     */
    List<VehicleObjectLineDto> getVehicleObjectLineVer(long vehicleCode, Long hisId, Integer verState, Integer... flg);

}
