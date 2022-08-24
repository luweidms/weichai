package com.youming.youche.record.provider.mapper.vehicle;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.record.domain.vehicle.VehicleAccident;
import com.youming.youche.record.vo.VehicledentAccidentVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author hzx
 * @description 车辆事故
 * @date 2022/1/15 16:16
 */
public interface VehicleAccidentMapper extends BaseMapper<VehicleAccident> {

    Integer insertAccidentRecord(@Param("vehicleAccident") VehicleAccident vehicleAccident);

    Page<VehicleAccident> queryAllRecord(Page<VehicleAccident> objectPage,
                                         @Param("vehicleCode") String vehicleCode, @Param("accidentStatus") String accidentStatus,
                                         @Param("reportDateStart") String reportDateStart, @Param("reportDateEnd") String reportDateEnd,
                                         @Param("accidentDateStart") String accidentDateStart, @Param("accidentDateEnd") String accidentDateEnd,
                                         @Param("createDateStart") String createDateStart, @Param("createDateEnd") String createDateEnd,
                                         @Param("tenantId") Long tenantId);

    List<VehicleAccident> queryAllRecordExport(@Param("vehicleCode") String vehicleCode, @Param("accidentStatus") String accidentStatus,
                                               @Param("reportDateStart") String reportDateStart, @Param("reportDateEnd") String reportDateEnd,
                                               @Param("accidentDateStart") String accidentDateStart, @Param("accidentDateEnd") String accidentDateEnd,
                                               @Param("createDateStart") String createDateStart, @Param("createDateEnd") String createDateEnd,
                                               @Param("tenantId") Long tenantId);

    /**
     * 修改事故\定损、核赔
     */
    Integer updateAccidentById(@Param("vehicleAccident") VehicleAccident vehicleAccident);

    /**
     * 查询事故详情
     */
    VehicleAccident selectAccidentById(@Param("id") Long id);

    Page<VehicledentAccidentVo> getVehicledentAccident(Page<VehicledentAccidentVo> page,
                                                       @Param("monthTimeStart") String monthTimeStart,
                                                       @Param("monthTimeEnd") String monthTimeEnd,
                                                       @Param("licenceType") Integer licenceType,
                                                       @Param("accidentStatus") Integer accidentStatus,
                                                       @Param("tenantId") Long tenantId);

    List<VehicledentAccidentVo> getVehicledentAccidentExport(@Param("monthTimeStart") String monthTimeStart,
                                                             @Param("monthTimeEnd") String monthTimeEnd,
                                                             @Param("licenceType") Integer licenceType,
                                                             @Param("accidentStatus") Integer accidentStatus,
                                                             @Param("tenantId") Long tenantId);

    /**
     * 车辆报表获取车辆事故上个月理赔金额
     */
    Long getVehicleClaimAmountByMonth(@Param("vehicleCode") Long vehicleCode, @Param("tenantId") Long tenantId, @Param("month") String month);

}
