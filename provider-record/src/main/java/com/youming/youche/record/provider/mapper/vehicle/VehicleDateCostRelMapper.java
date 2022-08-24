package com.youming.youche.record.provider.mapper.vehicle;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.vehicle.VehicleDateCostRel;
import com.youming.youche.record.dto.trailer.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

@MapperScan
public interface VehicleDateCostRelMapper extends BaseMapper<VehicleDateCostRel> {


    List<VehicleDateCostRelDto> queryAll(Long tenantId);



    List<DateCostDto> queryByType();

    List<DateCostDto> queryByTyperTwo();

    List<TrailerGuaCarDto> queryGua(Long tenantId);

    List<TrailerGuaPeiZhiDto> queryAllDto();

    void insertS(VehicleDateCostRel vehicleDateCostRel);

    void insertSs(VehicleDateCostRel vehicleDateCostRel);

    void insertSss(VehicleDateCostRel vehicleDateCostRel);

    List<ZcVehicleTrailerDto> queryZcXq();

    void deleteAll();

    List<DateCostDto> selectAssetDetails(Long tenantId);
}
