package com.youming.youche.record.provider.mapper.vehicle;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.vehicle.VehicleOrderPositionInfo;
import org.apache.ibatis.annotations.Param;

/**
 * @Date:2021/12/28
 */
public interface VehicleOrderPositionInfoMapper extends BaseMapper<VehicleOrderPositionInfo> {

    /***
     * @Description: 查询车辆是否有定位信息
     * @Author: luwei
     * @Date: 2022/8/24 14:48
     * @Param vehicleCode: 车牌号
     * @return: java.lang.Integer 大于0表示有定位
     * @Version: 1.0
     **/
    Integer countOrderPositionInfo(@Param("vehicleCode") Long vehicleCode);
}
