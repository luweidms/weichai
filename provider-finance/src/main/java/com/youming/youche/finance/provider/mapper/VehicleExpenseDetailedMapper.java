package com.youming.youche.finance.provider.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.finance.domain.VehicleExpenseDetailed;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.domain.vehicle.VehicleMiscellaneouFeeDto;
import com.youming.youche.finance.dto.GetVehicleExpenseDto;
import com.youming.youche.finance.vo.GetVehicleExpenseVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* <p>
* 车辆费用明细表Mapper接口
* </p>
* @author liangyan
* @since 2022-04-19
*/
    public interface VehicleExpenseDetailedMapper extends BaseMapper<VehicleExpenseDetailed> {

    List<GetVehicleExpenseVo> getVehicleExpense(@Param("applyNo") String applyNo, @Param("tenantId") Long tenantId);

    IPage<GetVehicleExpenseVo> getVehicleExpenseDetailedList(Page pae,@Param("getVehicleExpenseDto") GetVehicleExpenseDto getVehicleExpenseDto);

    Long getSumApplyAmount(@Param("getVehicleExpenseDto") GetVehicleExpenseDto getVehicleExpenseDto);

    Long sumApplyAmount(@Param("getVehicleExpenseDto") GetVehicleExpenseDto getVehicleExpenseDto);

    /**
     * 车帘报表获取车联费用数据
     */
    List<VehicleMiscellaneouFeeDto> getVehicleFeeByMonth(@Param("plateNumber") String plateNumber, @Param("tenantId") Long tenantId, @Param("month") String month);

}
