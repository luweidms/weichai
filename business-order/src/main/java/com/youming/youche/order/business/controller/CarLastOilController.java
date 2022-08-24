package com.youming.youche.order.business.controller;

import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.order.api.oil.ICarLastOilService;
import com.youming.youche.order.domain.oil.CarLastOil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: luona
 * @date: 2022/5/13
 * @description: T0DO
 * @version: 1.0
 */
@RestController
@RequestMapping("car/last/oil")
public class CarLastOilController extends BaseController<CarLastOil, ICarLastOilService> {
    @DubboReference(version = "1.0.0")
    ICarLastOilService carLastOilService;
    @Override
    public ICarLastOilService getService() {
        return null;
    }

    /**
     * 获取车辆最后使用油卡(30011)
     * @param plateNumber
     * @param tenantId
     * @return
     */
    @GetMapping("/getCarLastOilByPlateNumber")
    public ResponseResult getCarLastOilByPlateNumber(String plateNumber, Long tenantId){
        CarLastOil carLastOilByPlateNumber = carLastOilService.getCarLastOilByPlateNumber(plateNumber, tenantId);
        return ResponseResult.success(carLastOilByPlateNumber);
    }

}
