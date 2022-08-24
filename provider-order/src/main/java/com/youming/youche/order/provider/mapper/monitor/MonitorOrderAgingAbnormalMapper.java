package com.youming.youche.order.provider.mapper.monitor;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.order.domain.monitor.MonitorOrderAgingAbnormal;
import com.youming.youche.record.dto.LicenseDto;
import com.youming.youche.record.vo.LicenseVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 异常数据
 *
 * @author hzx
 * @date 2022/3/9 11:33
 */
public interface MonitorOrderAgingAbnormalMapper extends BaseMapper<MonitorOrderAgingAbnormal> {

    List<MonitorOrderAgingAbnormal> queryAgingAbnormalList(@Param("isOrderHis") String isOrderHis, @Param("isHis") String isHis,
                                                           @Param("orderId") Long orderId, @Param("type") Integer type,
                                                           @Param("tenantId") Long tenantId, @Param("sourceRegion") Integer sourceRegion,
                                                           @Param("desRegion") Integer desRegion, @Param("orgId") Long orgId,
                                                           @Param("plateNumber") String plateNumber);

    /**
     * 查询租户下车辆数量
     *
     * @param type       类型 0-全部，1-自有车，3-外调车，6-挂车
     * @param tenantId   租户编号
     * @param vehicleNum 车牌号（模糊匹配）
     * @param orgId      部门编号  传0表示查询所有
     */
    int queryVehicleCount(@Param("type") Integer type, @Param("tenantId") long tenantId,
                          @Param("vehicleNum") String vehicleNum, @Param("orgId") Long orgId);

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

    Integer getValidityTrailerCountQuery(@Param("fieldName") String fieldName, @Param("breifName") String breifName,
                                         @Param("tenantId") Long tenantId, @Param("plateNumber") String plateNumber,
                                         @Param("expireDate") Date expireDate);

    Integer getSearchVehicleCountQuery(@Param("fieldName") String fieldName, @Param("breifName") String breifName,
                                       @Param("tenantId") Long tenantId, @Param("plateNumber") String plateNumber,
                                       @Param("expireDate") Date expireDate, @Param("vehicleClass") Integer vehicleClass,
                                       @Param("state") Integer state);

    Integer getSearchTrailerCountQuery(@Param("fieldName") String fieldName, @Param("breifName") String breifName,
                                       @Param("tenantId") Long tenantId, @Param("plateNumber") String plateNumber,
                                       @Param("expireDate") Date expireDate);

    /**
     * 查询挂车订单保养车辆列表
     */
    List<Map> queryMaintainWarningTrailer(@Param("tenantId") Long tenantId, @Param("orgId") Long orgId,
                                          @Param("plateNumber") String plateNumber);

    /**
     * 查询自有车订单保养车辆列表
     *
     * @param tenantId    车队ID
     * @param orgId       运营区ID
     * @param plateNumber 车牌号
     */
    List<Map> queryMaintainWarningVehicle(@Param("tenantId") long tenantId, @Param("orgId") Long orgId,
                                          @Param("plateNumber") String plateNumber);

    /**
     * 查询保养车辆列表
     *
     * @param tenantId    车队ID
     * @param plateNumber 车牌号
     */
    List<Map> queryMaintainWarningVehicleBY(@Param("tenantId") long tenantId, @Param("plateNumber") String plateNumber);
    List<Map> queryMaintainWarningVehicleWX(@Param("tenantId") long tenantId, @Param("plateNumber") String plateNumber);

    List<Map> queryAgingDependPlateNumberList(@Param("isOrderHis") Boolean isOrderHis, @Param("isHis") Boolean isHis,
                                              @Param("orderId") Long orderId, @Param("type") Integer type,
                                              @Param("tenantId") Long tenantId, @Param("sourceRegion") Integer sourceRegion,
                                              @Param("desRegion") Integer desRegion, @Param("orgId") Long orgId,
                                              @Param("plateNumber") String plateNumber);

    List<Map> queryAgingAbnormalPlateNumberList(@Param("isOrderHis") boolean isOrderHis, @Param("isHis") boolean isHis,
                                                @Param("orderId") Long orderId, @Param("type") Integer type,
                                                @Param("tenantId") Long tenantId, @Param("sourceRegion") Integer sourceRegion,
                                                @Param("desRegion") Integer desRegion, @Param("orgId") Long orgId,
                                                @Param("plateNumber") String plateNumber);

    List<Map> queryAgingArrivePlateNumberList(@Param("isOrderHis") boolean isOrderHis, @Param("isHis") boolean isHis,
                                              @Param("orderId") Long orderId, @Param("type") Integer type,
                                              @Param("tenantId") Long tenantId, @Param("sourceRegion") Integer sourceRegion,
                                              @Param("desRegion") Integer desRegion, @Param("orgId") Long orgId,
                                              @Param("plateNumber") String plateNumber);

    /**
     * 行驶证审、交强险、商业险、其他险、营运证审 到期查询
     */
    List<LicenseVo> queryMaturityDoc(@Param("tenantId") Long tenantId, @Param("plateNumber") String plateNumber);


}
