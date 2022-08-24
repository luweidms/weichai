package com.youming.youche.market.provider.service.youka;

import com.youming.youche.market.api.youka.IOilCardVehicleRelService;
import com.youming.youche.market.domain.youka.OilCardVehicleRel;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.market.provider.mapper.youka.OilCardVehicleRelMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;


/**
* <p>
    * 油卡-车辆关系表 服务实现类
    * </p>
* @author XXX
* @since 2022-03-24
*/
@DubboService(version = "1.0.0")
    public class OilCardVehicleRelServiceImpl extends BaseServiceImpl<OilCardVehicleRelMapper, OilCardVehicleRel> implements IOilCardVehicleRelService {


    }
