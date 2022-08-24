package com.youming.youche.record.provider.mapper.apply;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.vo.VehicleApplyRecordVo;
import com.youming.youche.record.domain.apply.ApplyRecord;
import com.youming.youche.record.dto.ApplyRecordVehicleDto;
import com.youming.youche.record.dto.ApplyRecordVehicleInfoDto;
import com.youming.youche.record.dto.VehicleApplyRecordDto;
import com.youming.youche.record.vo.InvitationVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 申请记录表，会员跟车辆统一Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
public interface ApplyRecordMapper extends BaseMapper<ApplyRecord> {

    Long getInvitationCount(@Param("tenantId")Long tenantId);

    Long getInvitationCountTO(@Param("tenantId")Long tenantId);

    Page<InvitationVo>doQueryVehicleApplyRecordAll(Page<InvitationVo> page,
                                                   @Param("tenantId")Long tenantId,
                                                   @Param("applyType")Integer applyType,
                                                   @Param("plateNumber")String plateNumber,
                                                   @Param("tenantName")String tenantName,
                                                   @Param("linkPhone")String linkPhone,
                                                   @Param("driverMobile")String driverMobile,
                                                   @Param("driverName")String driverName,
                                                   @Param("state")Integer state,
                                                   @Param("applyVehicleClass")Integer applyVehicleClass);

    Page<InvitationVo>doQueryVehicleApplyRecordAllTo(Page<InvitationVo> page,@Param("tenantId")Long tenantId,
                                                   @Param("applyType")Integer applyType,
                                                   @Param("plateNumber")String plateNumber,
                                                   @Param("tenantName")String tenantName,
                                                   @Param("linkPhone")String linkPhone,
                                                   @Param("driverMobile")String driverMobile,
                                                   @Param("driverName")String driverName,
                                                   @Param("state")Integer state,
                                                   @Param("applyVehicleClass")Integer applyVehicleClass);

    /**
     * 车队小程序 -- 车辆邀请
     */
    List<InvitationVo> doQueryVehicleApplyRecordAllToWX(@Param("tenantId") Long tenantId,
                                                        @Param("applyType") Integer applyType,
                                                        @Param("plateNumber") String plateNumber,
                                                        @Param("tenantName") String tenantName,
                                                        @Param("linkPhone") String linkPhone,
                                                        @Param("driverMobile") String driverMobile,
                                                        @Param("driverName") String driverName,
                                                        @Param("state") String state,
                                                        @Param("applyVehicleClass") String applyVehicleClass);

    List<ApplyRecord> getApplyRecordList(@Param("busiId")Long busiId,
                                         @Param("state")Integer state,
                                         @Param("applyTenantId")Long applyTenantId);

    /**
     * 查询申请记录
     *
     * @param vehicleCode 业务主键，审核表主键id业务主键，审核表主键id
     * @param tenantId    邀请租户ID
     * @return
     */
    Map<String,Object> getVehicleRecordInfo(@Param("vehicleCode")Long vehicleCode,
                                            @Param("tenantId")Long tenantId);

    /**
     * 查询邀请详细信息
     *
     * @param applyRecord 邀请主键id
     * @return
     */
    VehicleApplyRecordVo getVehicleApplyRecord(@Param("applyRecord")Long applyRecord);

    /**
     * 查询符合条件的邀请记录数
     *
     * @param tenantId     邀请车队id
     * @param vehicleClass 类型，申请车辆类型
     * @param vehicleCode  业务主键
     * @return
     */
    Long getApplyCount(@Param("tenantId")Long tenantId,
                       @Param("vehicleClass")Integer vehicleClass,
                       @Param("vehicleCode")Long vehicleCode);

    /**
     * 查询符合条件的车辆邀请记录数
     *
     * @param tenantId     邀请租户ID
     * @param vehicleClass 类型，申请车辆类型
     * @param driverUserId 司机用户编号
     * @return
     */
    Long getApplyVehicleCountByDriverUserId(@Param("tenantId")Long tenantId,
                                            @Param("vehicleClass")Integer vehicleClass,
                                            @Param("driverUserId")Long driverUserId);

    /**
     * 查看车辆邀请详情-小程序接口
     */
    VehicleApplyRecordDto getVehicleApplyRecordNew(@Param("applyRecordId") Long applyRecordId, @Param("recordType") Integer recordType, @Param("tenantId") Long tenantId);

    ApplyRecordVehicleDto getApplyRecord(@Param("applyId") Long applyId);

    List<ApplyRecordVehicleInfoDto> getApplyRecordVehicle(@Param("tenantId") Long tenantId, @Param("userId") Long userId);

}
