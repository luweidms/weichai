package com.youming.youche.record.provider.mapper.vehicle;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.record.domain.service.ServiceRepairOrder;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.record.dto.vehicle.VehicleReportVehicleDataDto;
import com.youming.youche.record.dto.*;
import com.youming.youche.record.vo.HistoricalArchivesVo;
import com.youming.youche.record.vo.VehicleDataInfoVo;
import com.youming.youche.record.vo.VehicleDataInfoiVo;
import com.youming.youche.record.vo.VehicleDriverVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 车辆表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
public interface VehicleDataInfoMapper extends BaseMapper<VehicleDataInfo> {

    VehicleInfoDto getTenantVehicleInfo(@Param("vehicleClass") Integer vehicleClass,
                                        @Param("vehicleCode") Long vehicleCode,
                                        @Param("tenantId") Long tenantId);

    List<VehicleInfoDto> getAllVehicleInfoVerHis(@Param("vehicleClass") Integer vehicleClass,
                                                 @Param("vehicleCode") Long vehicleCode,
                                                 @Param("tenantId") Long tenantId);

    Map<String, Object> getVehicleIsRegister(@Param("vehicleClass") Integer vehicleClass,
                                             @Param("plateNumber") String plateNumber);

    Long getVehicleCountByDriverUserId(@Param("driverUserId") Long driverUserId,
                                       @Param("tenantId") Long tenantId);

    Page<HistoricalArchivesVo> doQueryVehicleAllHis(Page<HistoricalArchivesVo> page,
                                                    @Param("tenantId") Long tenantId,
                                                    @Param("verState") Integer verState,
                                                    @Param("plateNumber") String plateNumber,
                                                    @Param("linkManName") String linkManName,
                                                    @Param("tenantName") String tenantName,
                                                    @Param("linkPhone") String linkPhone,
                                                    @Param("linkman") String linkman,
                                                    @Param("mobilePhone") String mobilePhone,
                                                    @Param("vehicleLength") String vehicleLength,
                                                    @Param("vehicleStatus") Integer vehicleStatus,
                                                    @Param("vehicleClass") Integer vehicleClass);


    VehicleDataInfo getVehicleDataInfo(@Param("plateNumber") String plateNumber);

    VehicleDataInfoVo getShareVehicle(@Param("vhicleCode") Long vhicleCode,
                                      @Param("tenantId") Long tenantId);

    VehicleDataInfoVo getShareVehicleDataInfo(@Param("vhicleCode") Long vhicleCode,
                                              @Param("tenantId") Long tenantId);


    List<VehicleDataInfo> getVehicle(@Param("userId") Long userId,
                                     @Param("tenantId") Long tenantId);

    Long maxId();

    String selectPlateNumberById(@Param("vhicleCode") Long vhicleCode);

    List<VehicleDataInfo> getDriverAllRelVehicleList(@Param("driverUserId") Long driverUserId);

    Integer doUpdateVehicleObjectLine(@Param("vehicleCode") Long vehicleCode);

    Integer doUpdateVehicleLineRelByVehicleCode(@Param("vehicleCode") Long vehicleCode);


    Integer doUpdateVehicleLineRelVerByVehicleCode(@Param("vehicleCode") Long vehicleCode);


    Integer doDelVehicleOrderPositionInfo(@Param("vehicleCode") Long vehicleCode);


    Page<VehicleDataInfoiVo> doQueryAllShareVehicle(Page<VehicleDataInfoiVo> pageInfo,
                                                    @Param("vehicleDataInfoiDto") VehicleDataInfoiDto vehicleDataInfoiDto,
                                                    @Param("flg") Boolean flg);

    Page<VehicleDataInfoiVo> getVehicleDataInfoPlateNumber(Page<VehicleDataInfoiVo> page, @Param("plateNumber") String plateNumber);

    Integer existsDriver(@Param("userId") Long userId, @Param("tenantId") Long tenantId, @Param("carUserType") Long carUserType);

    List<VehicleDriverVo> getVehicleDriver(@Param("plateNumber") String plateNumber);

    Map getVehicleIsRegisterForOBMS(@Param("plateNumber") String plateNumber, @Param("vehicleClass") Integer vehicleClass);

    OBMSVehicleInfoDto getTenantVehicleInfoOBMS(@Param("vehicleClass") Integer vehicleClass, @Param("vehicleCode") Long vehicleCode);


    VehicleDataInfoVxDto getVehicleByPlateNumerVx(@Param("plateNumber") String plateNumber);

    List<VehicleDataInfoVxVo> getVehicleByPlateNumer(@Param("plateNumber") String plateNumber,
                                                     @Param("tenantId") Long tenantId);


    List<DriverDataInfoDto> doQueryCarDriver(@Param("loginAcct") String loginAcct,
                                             @Param("tenantId") Long tenantId);

    Page<VehicleDataInfoVxVo> getVehicleByPlateNumerPage(Page<VehicleDataInfoVxVo> vo, @Param("plateNumber") String plateNumber,
                                                         @Param("tenantId") Long tenantId);

    List<Map> getVehicleByDriverUserId(@Param("driverUserId") Long driverUserId);

    List<OrderCountDto> getOrderCountByVehicleCode(@Param("vehicleCode") Long vehicleCode);

    List<OrderCountDto> getOrderCountHByVehicleCode(@Param("vehicleCode") Long vehicleCode);

    Long getOrderCountLByVehicleCode(@Param("vehicleCode") Long vehicleCode, @Param("tenantId") Long tenantId);

    Long getOrderCountLHByVehicleCode(@Param("vehicleCode") Long vehicleCode, @Param("tenantId") Long tenantId);

    List<QueryTenantDto> doQueryTenantByVehicleCodeApp(@Param("vehicleCode") Long vehicleCode);

    VehicleInfoAppDto getTenantVehicleInfoApp(@Param("vehicleCode") Long vehicleCode);

    List<QueryVehicleByDriverDto> doQueryVehicleByDriver(@Param("driverUserId") Long driverUserId);

    /**
     * 维修保养工单列表(82014)
     *
     * @param page
     * @param orderStatus
     * @return
     */
    Page<ServiceRepairOrder> doQueryOrderListApp(Page<ServiceRepairOrder> page, @Param("orderStatus") Integer orderStatus, @Param("tenantId") Long tenantId, @Param("carDriverPhone") Long carDriverPhone);

    /**
     * 获取车辆报表所需车辆数据
     */
    List<VehicleReportVehicleDataDto> getVehicleDataAll();

}
