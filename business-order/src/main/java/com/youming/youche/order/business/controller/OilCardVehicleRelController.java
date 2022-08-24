package com.youming.youche.order.business.controller;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.order.api.IOilCardVehicleRelService;
import com.youming.youche.order.domain.OilCardVehicleRel;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>
* 油卡-车辆关系表 前端控制器
* </p>
* @author liangyan
* @since 2022-03-07
*/
@RestController
@RequestMapping("/order/oilCard")
public class OilCardVehicleRelController extends BaseController<OilCardVehicleRel, IOilCardVehicleRelService> {

    @DubboReference(version = "1.0.0")
    private IOilCardVehicleRelService oilCardVehicleRelService;
    @Override
    public IOilCardVehicleRelService getService() {
        return oilCardVehicleRelService;
    }

}
