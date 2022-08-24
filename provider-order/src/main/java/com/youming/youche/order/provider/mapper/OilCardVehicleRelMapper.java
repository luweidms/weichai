package com.youming.youche.order.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.OilCardManagement;
import com.youming.youche.order.domain.OilCardVehicleRel;

import java.util.List;

/**
* <p>
* 油卡-车辆关系表Mapper接口
* </p>
* @author liangyan
* @since 2022-03-07
*/
    public interface OilCardVehicleRelMapper extends BaseMapper<OilCardVehicleRel> {

    //通过车牌号码查询油卡
    List<OilCardManagement> findByPlateNumber(String plateNumber, Long tenantId);

    }
