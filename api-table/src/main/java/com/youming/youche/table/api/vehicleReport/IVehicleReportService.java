package com.youming.youche.table.api.vehicleReport;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.table.domain.vehicleReport.VehicleReport;
import com.youming.youche.table.domain.workbench.BossWorkbenchInfo;
import com.youming.youche.table.dto.vehiclereport.VehicleReportDto;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wuhao
 * @since 2022-05-06
 */
public interface IVehicleReportService extends IBaseService<VehicleReport> {

    /**
     * 定时任务(月报)
     */
    void execute(String lastDateStr);

    /**
     * 车辆报表列表查询
     */
    PageInfo<VehicleReport> queryVehicleReportData(String carNumber, String startMonth, String endMonth, String currentLine,
                                                      String department, Integer pageSize, Integer pageNum, String accessToken);

    /**
     * 车辆报表列表导出
     */
    void CarDownload(String accessToken, ImportOrExportRecords record, String carNumber, String startMouth, String endMonth, String currentLine, String department);

    /**
     * 老板工作台  获取车辆费用数据
     */
    List<BossWorkbenchInfo> getTableVehicleList();

}
