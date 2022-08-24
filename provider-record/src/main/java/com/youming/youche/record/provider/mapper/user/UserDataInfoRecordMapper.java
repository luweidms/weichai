package com.youming.youche.record.provider.mapper.user;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.record.domain.user.UserDataInfo;
import com.youming.youche.record.dto.ApplyRecordQueryDto;
import com.youming.youche.record.dto.StaffDataInfoDto;
import com.youming.youche.record.dto.driver.DoQueryDriversDto;
import com.youming.youche.record.dto.driver.DoQueryOBMDriversDto;
import com.youming.youche.record.dto.driver.DriverQueryDto;
import com.youming.youche.record.vo.ApplyRecordQueryVo;
import com.youming.youche.record.vo.ApplyRecordVo;
import com.youming.youche.record.vo.BeApplyRecordVehicleVo;
import com.youming.youche.record.vo.BeApplyRecordVo;
import com.youming.youche.record.vo.StaffDataInfoVo;
import com.youming.youche.record.vo.UserDataInfoBackVo;
import com.youming.youche.record.vo.driver.DoQueryDriversVo;
import com.youming.youche.record.vo.driver.DoQueryOBMDriversVo;
import com.youming.youche.record.vo.driver.DriverApplyRecordVo;
import com.youming.youche.record.vo.driver.DriverQueryVo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户资料信息Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2021-11-17
 */
public interface UserDataInfoRecordMapper extends BaseMapper<UserDataInfo> {

    /**
     * 分页查询司机档案
     *
     * @param tenantId
     * @param userType
     * @param expiredDate
     * @return
     */
    Page<DoQueryDriversDto> doQueryCarDriver(Page<DoQueryDriversDto> page, @Param("tenantId") Long tenantId, @Param("userType") String userType,
                                             @Param("expiredDate") String expiredDate, @Param("doQueryDriversVo") DoQueryDriversVo doQueryDriversVo);

    /**
     * 分页查询司机档案(小程序)
     *
     * @param page
     * @param tenantId
     * @param userType
     * @param expiredDate
     * @param doQueryDriversVo
     * @param carUserTypeStr   新修改需求（需添加招商车和外调车）
     * @return
     */
    Page<DoQueryDriversDto> doQueryCarDriverWx(Page<DoQueryDriversDto> page, @Param("tenantId") Long tenantId, @Param("userType") String userType,
                                               @Param("expiredDate") String expiredDate, @Param("doQueryDriversVo") DoQueryDriversVo doQueryDriversVo,
                                               @Param("carUserTypeStr") String carUserTypeStr);

    /**
     * 导出条件查询司机档案
     *
     * @param tenantId
     * @param userType
     * @param expiredDate
     * @return
     */
    List<DoQueryDriversDto> doQueryCarDriverExport(@Param("tenantId") Long tenantId, @Param("userType") String userType,
                                                   @Param("expiredDate") String expiredDate, @Param("doQueryDriversVo") DoQueryDriversVo doQueryDriversVo);

    Page<DoQueryOBMDriversDto> doQueryOBMCarDriver(Page<DoQueryOBMDriversDto> page, @Param("hasVer") Integer hasVer,
                                                   @Param("doQueryOBMDriversVo") DoQueryOBMDriversVo doQueryOBMDriversVo);

    Long getDriverTenantId(@Param("userId") long userId);

    Map getUserQuery(@Param("id") Long id, @Param("tenantId") Long tenantId);

    /**
     * 根据userId获取员工姓名
     */
    List<String> querStaffName(@Param("userIdList") String userIdList);

    List<Map> getVehicle(@Param("userId") Long userId, @Param("tenantId") Long tenantId);

    Page<ApplyRecordVo> doQueryApplyRecords(Page<ApplyRecordVo> page, @Param("applyRecordQueryDto") ApplyRecordQueryDto applyRecordQueryDto, @Param("tenantId") Long tenantId);

    Page<ApplyRecordVo> doQueryBeApplyRecords(Page<ApplyRecordVo> page, @Param("applyRecordQueryDto") ApplyRecordQueryDto applyRecordQueryDto, @Param("tenantId") Long tenantId);

    Page<DriverQueryVo> doQueryDriverHis(Page<DriverQueryVo> page, @Param("driverQueryDto") DriverQueryDto driverQueryDto, @Param("tenantId") Long tenantId);

    Page<UserDataInfoBackVo> queryAllTenantDriverOrPtDriver(Page<UserDataInfoBackVo> page,
                                                            @Param("linkman") String linkman,
                                                            @Param("loginAcct") String loginAcct,
                                                            @Param("tenantId") Long tenantId);

    UserDataInfo queryUserInfoByMobile(@Param("mobilePhone") String mobilePhone);


    DriverApplyRecordVo selectDriverApplyRecordVo(@Param("applyId") Long applyId);


    BeApplyRecordVo getBeApplyRecord(@Param("applyId") Long applyId);

    List<BeApplyRecordVehicleVo> getBeApplyRecordVehicle(@Param("userId")Long userId,@Param("tenantId")Long tenantId);




    UserDataInfo getUserDataInfoByMoblile(String mobilePhone);

    List<StaffDataInfoDto> queryStaffInfo(@Param("staffDataInfoIn") StaffDataInfoVo staffDataInfoIn,
                                          @Param("tenantId") Long tenantId);

    Page<ApplyRecordVo> doQueryBeApplyRecordsNew(Page<ApplyRecordVo> page, @Param("applyRecordQueryVo") ApplyRecordQueryVo applyRecordQueryVo,
                                                 @Param("tenantId") Long tenantId);

    Page<StaffDataInfoDto> queryStaffInfoPage(Page<StaffDataInfoDto> page, @Param("staffDataInfoIn") StaffDataInfoVo staffDataInfoIn,
                                          @Param("tenantId") Long tenantId);

    List<WorkbenchDto> getTableUserDriverCount();

    List<WorkbenchDto> getTableUserQcCertiCount(@Param("localDateTime") LocalDateTime localDateTime);

    /**
     * 营运工作台  司机档案数量
     */
    List<WorkbenchDto> getTableDriverCount();

}
