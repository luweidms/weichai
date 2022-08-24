package com.youming.youche.finance.provider.service;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.finance.api.IVehicleExpenseDetailedVerService;
import com.youming.youche.finance.domain.VehicleExpenseDetailedVer;
import com.youming.youche.finance.provider.mapper.VehicleExpenseDetailedVerMapper;
import org.apache.dubbo.config.annotation.DubboService;


/**
* <p>
    * 车辆费用明细表  历史表 服务实现类
    * </p>
* @author liangyan
* @since 2022-04-25
*/
@DubboService(version = "1.0.0")
public class VehicleExpenseDetailedVerServiceImpl extends BaseServiceImpl<VehicleExpenseDetailedVerMapper, VehicleExpenseDetailedVer> implements IVehicleExpenseDetailedVerService {


}
