package com.youming.youche.finance.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.finance.domain.VehicleExpense;
import com.youming.youche.finance.dto.GetVehicleExpenseDto;
import com.youming.youche.finance.dto.VehicleListByDriverDto;
import com.youming.youche.finance.vo.GetVehicleExpenseVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 车辆费用表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2022-01-23
 */
public interface VehicleExpenseMapper extends BaseMapper<VehicleExpense> {

    List<GetVehicleExpenseVo>getVehicleExpenseVoByPage(@Param("getVehicleExpenseDto") GetVehicleExpenseDto getVehicleExpenseDto,
                                                        @Param("lids") List<Long> lids );

    /**
     * 营运工作台  车辆费用 待我审
     */
    List<WorkbenchDto> getTableVehicleCostCount();

    /**
     * 营运工作台  车辆费用 我的
     */
    List<WorkbenchDto> getTableVehicleCostMeCount();


    List<VehicleListByDriverDto> selectOr(@Param("driverUserId")Long driverUserId);

    /**
     * 车辆待审核记录数
     */
    Integer getCarCostReportAuditCount(@Param("tenantId") Long tenantId, @Param("lids") List<Long> lids);

}
