package com.youming.youche.record.api.vehicle;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.record.domain.vehicle.VehicleAnnualReview;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;

import java.util.List;

/**
 * @description 车辆年审
 * @date 2022/1/14 16:44
 */
public interface IVehicleAnnualReviewService extends IBaseService<VehicleAnnualReview> {

    /**
     * 新增车帘年审
     */
    Integer insertOrUpdate(VehicleAnnualReview vehicleAnnualReview, String accessToken);

    /**
     * 判断车辆是否存在牌照类型和车队
     */
    Integer exitsVehicleCode(String vehicleCode);

    /**
     * 分页查询车辆年审列表
     */
    Page<VehicleAnnualReview> getVehicleAnnualReviewList(Page<VehicleAnnualReview> objectPage, VehicleAnnualReview vehicleAnnualReview, String accessToken);

    /**
     * 车辆年审列表导出
     */
    void getVehicleAnnualReviewListExport(VehicleAnnualReview vehicleAnnualReview, String accessToken, ImportOrExportRecords importOrExportRecords);

    /**
     * 查看年审详情
     */
    VehicleAnnualReview queryVehicleAnnualReviewDetails(Long id);

    /**
     * 车辆年审导入（批量新增）
     */
    void batchImport(byte[] bytes, String fileName, ImportOrExportRecords record, String accessToken);

    /**
     * 车辆年审预警数量
     */
    List<WorkbenchDto> getTableVehicleCount();

    /**
     * 年审：新增年审记录车辆查询，只录入自有车
     */
    Page<VehicleDataInfo> queryVehicleFromAnnualReview(String plateNumber, String accessToken);

    /**
     * 车辆费用获取车辆上个月年审费用
     */
    Long getVehicleAnnualreviewCostByMonth(Long vehicleCode, Long tenantId, String month);

}
