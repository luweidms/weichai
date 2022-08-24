package com.youming.youche.finance.business.controller;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.finance.api.IVehicleExpenseVerService;
import com.youming.youche.finance.domain.VehicleExpenseVer;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>
* 车辆费用表 历史表 前端控制器
* </p>
* @author liangyan
* @since 2022-04-25
*/
@RestController
@RequestMapping("vehicle/expense/ver")
public class VehicleExpenseVerController extends BaseController<VehicleExpenseVer, IVehicleExpenseVerService> {
    @DubboReference(version = "1.0.0")
    IVehicleExpenseVerService vehicleExpenseVerService;
    @Override
    public IVehicleExpenseVerService getService() {
        return vehicleExpenseVerService;
    }
}
