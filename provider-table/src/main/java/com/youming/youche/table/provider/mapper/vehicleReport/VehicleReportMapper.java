package com.youming.youche.table.provider.mapper.vehicleReport;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.table.domain.vehicleReport.VehicleReport;
import com.youming.youche.table.domain.workbench.BossWorkbenchInfo;
import com.youming.youche.table.dto.vehiclereport.VehicleReportDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper接口
 * </p>
 *
 * @author wuhao
 * @since 2022-05-06
 */
public interface VehicleReportMapper extends BaseMapper<VehicleReport> {
    /**
     * 车辆报表列表查询
     * @param tenantId
     * @param carNumber
     * @param startMonth
     * @param endMonth
     * @param currentLine
     * @param department
     * @return
     */
    List<VehicleReport> queryVehicleReportData( @Param("tenantId") Long tenantId, @Param("carNumber") String carNumber,
                                                  @Param("startMonth") String startMonth, @Param("endMonth") String endMonth,
                                                  @Param("currentLine") String currentLine,
                                                  @Param("department") String department);

    /**
     * 老板工作台  获取车辆费用数据
     */
    List<BossWorkbenchInfo> getTableVehicleList();
}
