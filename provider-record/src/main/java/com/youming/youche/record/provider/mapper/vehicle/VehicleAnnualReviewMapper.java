package com.youming.youche.record.provider.mapper.vehicle;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.record.domain.vehicle.VehicleAnnualReview;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description 车辆年审
 * @date 2022/1/14 16:42
 */
public interface VehicleAnnualReviewMapper extends BaseMapper<VehicleAnnualReview> {

    Integer customInsert(@Param("vehicleAnnualReview") VehicleAnnualReview vehicleAnnualReview);

    Integer customUpdate(@Param("vehicleAnnualReview") VehicleAnnualReview vehicleAnnualReview);

    Integer exitsVehicleCode(@Param("vehicleCode") String vehicleCode);

    Page<VehicleAnnualReview> getVehicleAnnualReviewList(Page<VehicleAnnualReview> objectPage,
                                                         @Param("vehicleAnnualReview") VehicleAnnualReview vehicleAnnualReview,
                                                         @Param("tenantId") Long tenantId);

    List<VehicleAnnualReview> getVehicleAnnualReviewListExport(@Param("vehicleAnnualReview") VehicleAnnualReview vehicleAnnualReview,
                                                               @Param("tenantId") Long tenantId);

    VehicleAnnualReview selectAnnualReviewById(@Param("id") Long id);

    List<WorkbenchDto> getTableVehicleCount(@Param("localDateTime") LocalDateTime localDateTime);

    /**
     * @param vehicleId     车辆id
     * @param effectiveDate 年审结束时间
     * @return
     */
    Integer judgeDataExist(@Param("vehicleId") Long vehicleId, @Param("effectiveDate") String effectiveDate, @Param("annualreviewType") String annualreviewType);

    /**
     * 判断申请单号是否重复
     *
     * @param requestNo 申请单号
     */
    Integer judgeRequestNoExist(@Param("requestNo") String requestNo, @Param("tenantId") Long tenantId);

    Page<VehicleDataInfo> queryVehicleFromAnnualReview(Page<VehicleDataInfo> page, @Param("plateNumber") String plateNumber, @Param("tenantId") Long tenantId, @Param("vehicleClass") Integer vehicleClass);

    /**
     * 车辆费用获取车辆上个月年审费用
     */
    Long getVehicleAnnualreviewCostByMonth(@Param("vehicleCode") Long vehicleCode, @Param("tenantId") Long tenantId, @Param("month") String month);

}
