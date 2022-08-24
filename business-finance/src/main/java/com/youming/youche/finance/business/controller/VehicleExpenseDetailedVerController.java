package com.youming.youche.finance.business.controller;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.finance.api.IVehicleExpenseDetailedVerService;
import com.youming.youche.finance.domain.VehicleExpenseDetailedVer;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>
* 车辆费用明细表  历史表 前端控制器
* </p>
* @author liangyan
* @since 2022-04-25
*/
@RestController
@RequestMapping("vehicle/expense/detailed/ver")
public class VehicleExpenseDetailedVerController extends BaseController<VehicleExpenseDetailedVer, IVehicleExpenseDetailedVerService> {

    @DubboReference(version = "1.0.0")
    IVehicleExpenseDetailedVerService vehicleExpenseDetailedVerService;
    @Override
    public IVehicleExpenseDetailedVerService getService() {
        return vehicleExpenseDetailedVerService;
    }
}
