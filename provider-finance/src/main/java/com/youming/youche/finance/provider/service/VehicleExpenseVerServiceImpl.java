package com.youming.youche.finance.provider.service;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.finance.api.IVehicleExpenseVerService;
import com.youming.youche.finance.domain.VehicleExpenseVer;
import com.youming.youche.finance.provider.mapper.VehicleExpenseVerMapper;
import org.apache.dubbo.config.annotation.DubboService;


/**
* <p>
    * 车辆费用表 历史表 服务实现类
    * </p>
* @author liangyan
* @since 2022-04-25
*/
@DubboService(version = "1.0.0")
public class VehicleExpenseVerServiceImpl extends BaseServiceImpl<VehicleExpenseVerMapper, VehicleExpenseVer> implements IVehicleExpenseVerService {


}
