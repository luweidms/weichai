package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IVehicleReturnInfoService;
import com.youming.youche.order.domain.order.VehicleReturnInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-25
 */
@RestController
@RequestMapping("vehicle-return-info")
public class VehicleReturnInfoController extends BaseController<VehicleReturnInfo, IVehicleReturnInfoService> {
    @DubboReference(version = "1.0.0")
    IVehicleReturnInfoService vehicleReturnInfoService;
    @Override
    public IVehicleReturnInfoService getService() {
        return vehicleReturnInfoService;
    }
}
