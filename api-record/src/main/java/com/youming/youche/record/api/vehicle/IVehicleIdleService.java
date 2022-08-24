package com.youming.youche.record.api.vehicle;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.record.domain.vehicle.VehicleIdle;
import com.youming.youche.record.vo.IdleVehiclesVo;

import java.util.List;

/**
 * <p>
 * 闲置车辆表 服务类
 * </p>
 *
 * @author wuhao
 * @since 2022-05-18
 */
public interface IVehicleIdleService extends IBaseService<VehicleIdle> {

    /***
     * @Description: 条件查询闲置车辆
     * @Author: luwei
     * @Date: 2022/5/18 5:34 下午
     * @Param idleVehiclesVo:
     * @return: java.util.List<com.youming.youche.record.domain.vehicle.VehicleIdle>
     * @Version: 1.0
     **/
    List<IdleVehiclesVo> queryIdleVehicles(IdleVehiclesVo idleVehiclesVo,String accessToken);

    /***
     * @Description: 车辆id查询闲置信息
     * @Author: luwei
     * @Date: 2022/5/19 12:02 上午

     * @return: java.util.List<com.youming.youche.record.domain.vehicle.VehicleIdle>
     * @Version: 1.0
     **/
    List<VehicleIdle> queryVehicleIdle(Long vid);

}
