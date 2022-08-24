package com.youming.youche.record.provider.mapper.tenant;


import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.record.domain.tenant.TenantVehicleRel;
import com.youming.youche.record.domain.tenant.TenantVehicleRelVer;
import com.youming.youche.record.vo.TenantVehicleRelVo;
import com.youming.youche.record.vo.VehicleDataVo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 车队与车辆关系表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
public interface TenantVehicleRelMapper extends BaseMapper<TenantVehicleRel> {

    @InterceptorIgnore(tenantLine = "true")
    Page<TenantVehicleRelVo> doQueryVehicleAll(Page<TenantVehicleRelVo> page,
                                               @Param("tenantId") Long tenantId,
                                               @Param("plateNumber") String plateNumber,
                                               @Param("linkManName") String linkManName,
                                               @Param("linkPhone") String linkPhone,
                                               @Param("billReceiverMobile") String billReceiverMobile,
                                               @Param("linkman") String linkman,
                                               @Param("tenantName") String tenantName,
                                               @Param("mobilePhone") String mobilePhone,
                                               @Param("authStateIn") Integer authStateIn,
                                               @Param("vehicleLength") String vehicleLength,
                                               @Param("vehicleStatus") Integer vehicleStatus,
                                               @Param("shareFlgIn") Integer shareFlgIn,
                                               @Param("vehicleClass") Integer vehicleClass,
                                               @Param("isAuth") Integer isAuth,
                                               @Param("vehicleGps") Integer vehicleGps);

    @InterceptorIgnore(tenantLine = "true")
    Page<TenantVehicleRelVo> doQueryVehicleAllIsOrder(@Param("orgId") Long orgId,Page<TenantVehicleRelVo> page,
                                               @Param("isWorking") Integer isWorking,
                                               @Param("tenantId") Long tenantId,
                                               @Param("plateNumber") String plateNumber,
                                               @Param("linkManName") String linkManName,
                                               @Param("linkPhone") String linkPhone,
                                               @Param("billReceiverMobile") String billReceiverMobile,
                                               @Param("linkman") String linkman,
                                               @Param("tenantName") String tenantName,
                                               @Param("mobilePhone") String mobilePhone,
                                               @Param("authStateIn") Integer authStateIn,
                                               @Param("vehicleLength") String vehicleLength,
                                               @Param("vehicleStatus") Integer vehicleStatus,
                                               @Param("shareFlgIn") Integer shareFlgIn,
                                               @Param("vehicleClass") Integer vehicleClass,
                                               @Param("isAuth") Integer isAuth,
                                               @Param("vehicleGps") Integer vehicleGps);

    List<TenantVehicleRel> getZYVehicleByVehicleCode(@Param("vehicleCode") Long vehicleCode,
                                                     @Param("vehicleClass") Integer vehicleClass);


    List<TenantVehicleRelVer> getZYVehicleByVehicleCodeVer(@Param("vehicleCode") Long vehicleCode,
                                                           @Param("vehicleClass") Integer vehicleClass);

    List<TenantVehicleRel> getTenantVehicleRel(@Param("vehicleCode") Long vehicleCode);

    List<TenantVehicleRel> getTenantVehicleRelList(@Param("vehicleCode") Long vehicleCode);

    List<TenantVehicleRel> getTenantVehicleRelListByPlateNumber(@Param("plateNumber") String plateNumber,
                                                                @Param("vehicleClass") Integer vehicleClass);

    TenantVehicleRel tenantVehicleRel(@Param("vehicleCode") Long vehicleCode,
                                      @Param("tenantId") Long tenantId);

    List<TenantVehicleRel> getTenantVehicleRelListByDriverUserId(@Param("driverUserId") Long driverUserId,
                                                                 @Param("tenantId") Long tenantId);

    List<TenantVehicleRel> queryTenantVehicleRel(@Param("tenantId") long tenantId, @Param("vehicleCode") String vehicleCode);

    Integer checkVehicleClass(@Param("plateNumer") String plateNumer,
                              @Param("tenantId") Long tenantId);

    Long maxId();

    Long getZYCount(@Param("vehicleCode") Long vehicleCode,
                    @Param("tenantId") Long tenantId,
                    @Param("vehicleClass") Integer vehicleClass);

    List<TenantVehicleRel> queryTenantVehicleRelDriver(@Param("vehicleCode") Long vehicleCode);

    List<VehicleDataVo> doQueryVehicleAllDis(@Param("authStateIn") Integer authStateIn,
                                             @Param("shareFlgIn") Integer shareFlgIn,
                                             @Param("isAuth") Integer isAuth,
                                             @Param("startTime") String startTime,
                                             @Param("endTime") String endTime,
                                             @Param("plateNumber") String plateNumber,
                                             @Param("linkManName") String linkManName,
                                             @Param("linkPhone") String linkPhone,
                                             @Param("linkman") String linkman,
                                             @Param("mobilePhone") String mobilePhone,
                                             @Param("tenantName") String tenantName,
                                             @Param("vehicleLength") String vehicleLength,
                                             @Param("vehicleStatus") Integer vehicleStatus);


    List<TenantVehicleRelVo> getVehicleAll(@Param("tenantId") Long tenantId,
                                           @Param("plateNumber") String plateNumber,
                                           @Param("linkManName") String linkManName,
                                           @Param("linkPhone") String linkPhone,
                                           @Param("billReceiverMobile") String billReceiverMobile,
                                           @Param("linkman") String linkman,
                                           @Param("tenantName") String tenantName,
                                           @Param("mobilePhone") String mobilePhone,
                                           @Param("authStateIn") Integer authStateIn,
                                           @Param("vehicleLength") String vehicleLength,
                                           @Param("vehicleStatus") Integer vehicleStatus,
                                           @Param("shareFlgIn") Integer shareFlgIn,
                                           @Param("vehicleClass") Integer vehicleClass,
                                           @Param("isAuth") Integer isAuth,
                                           @Param("vehicleGps") Integer vehicleGps);

    Integer upDriverUserIdNull(@Param(("id")) Long id);




   /**
            * 查询租户下车辆列表信息
     *
             * @param type       类型 0-全部，1-自有车，3-外调车，6-挂车
     * @param tenantId   租户编号
     * @param vehicleNum 车牌号（模糊匹配）
            * @param orgId      部门编号  传0表示查询所有
     * @return List<Map> (vehicleNum 车牌号，orgId 机构编号，vehicleClass 车辆类型， linkMan 主驾，linkPhone 主驾电话)
            */
    List<Map> queryVehicleList(@Param("type") int type, @Param("tenantId") long tenantId,
                               @Param("vehicleNum") String vehicleNum, @Param("orgId") int orgId);

    /**
     * 查询指定时限无单车辆
     *
     * @param vehicleClass 车辆类型
     * @param tenantId     车队ID
     * @param orgId        运营区ID
     * @param plateNumber  车牌号
     * @param hour         无单时限
     * @param hourEnd      无单时限
     * @param isCount      是否查询总数量
     */
    List<Map> queryNoneOrderVehicle(@Param("vehicleClass") Integer vehicleClass, @Param("tenantId") Long tenantId,
                                    @Param("orgId") Long orgId, @Param("plateNumber") String plateNumber,
                                    @Param("hour") int hour, @Param("hourEnd") int hourEnd, @Param("isCount") String isCount);


    /**
     * 营运工作台 交强险预警数量
     */
    List<WorkbenchDto> getTableCompulsoryInsuranceCount(@Param("localDateTime") LocalDateTime localDateTime);

    /**
     * 营运工作台 商业险预警数量
     */
    List<WorkbenchDto> getTableCommercialInsuranceCount(@Param("localDateTime") LocalDateTime localDateTime);

    /**
     * 营运工作台  自有车数量
     */
    List<WorkbenchDto> getTableOwnerCarCount();

    /**
     * 营运工作台  外调车数量
     */
    List<WorkbenchDto> getTableTemporaryCarCount();

    /**
     * 营运工作台  招商车数量
     */
    List<WorkbenchDto> getTableAttractCarCount();
}
