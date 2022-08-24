package com.youming.youche.record.provider.service.impl.vehicle;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.record.api.vehicle.IVehicleLineRelVerService;
import com.youming.youche.record.domain.vehicle.VehicleLineRelVer;
import com.youming.youche.record.provider.mapper.vehicle.VehicleLineRelVerMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * <p>
 * 车辆心愿线路关系版本 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@DubboService(version = "1.0.0")
public class VehicleLineRelVerServiceImpl extends ServiceImpl<VehicleLineRelVerMapper, VehicleLineRelVer>
		implements IVehicleLineRelVerService {

}
