package com.youming.youche.record.api.tenant;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.record.domain.tenant.TenantVehicleRel;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.record.vo.TenantVehicleRelVo;
import com.youming.youche.record.vo.VehicleDataVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 车队与车辆关系表 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
public interface ITenantVehicleRelService extends IService<TenantVehicleRel> {

    /**
     * 保存车队和车辆关系信息返回主键
     */
    Long saveById(TenantVehicleRel tenantVehicleRel);

    /**
     * 查询车队车辆信息
     *
     * @param vehicleCode   车帘编号
     * @param applyTenantId 车队id
     */
    List<TenantVehicleRel> getTenantVehicleRelList(long vehicleCode, Long applyTenantId);

    /**
     * 查询车队车辆信息
     *
     * @param vehicleCode 车帘编号
     */
    List<TenantVehicleRel> getTenantVehicleRelList(long vehicleCode);

    /**
     * 查询车队自由车车辆信息
     *
     * @param vehicleCode 车帘编号
     */
    TenantVehicleRel getZYVehicleByVehicleCode(long vehicleCode);

    /**
     * 查询车队车辆信息
     *
     * @param vehicleCode 车帘编号
     * @param tenantId    车队id
     */
    TenantVehicleRel getTenantVehicleRel(long vehicleCode, long tenantId);

    /**
     * 查询类型车辆信息
     *
     * @param vehicleCode  车辆编号
     * @param vehicleClass 车辆类型
     */
    TenantVehicleRel getTenantVehiclesRel(Long vehicleCode, Integer vehicleClass);

    /**
     * 查询车队自由车车辆信息
     *
     * @param vehicleCode 车帘编号
     */
    TenantVehicleRel getTenantVehicleRel(long vehicleCode);

    /**
     * 查询车队下用户所有车队车辆信息
     *
     * @param userId   用户编号
     * @param tenantId 车队id
     */
    List<TenantVehicleRel> getTenantVehicleRelByDriverUserId(long userId, long tenantId);

    /**
     * 查询车辆基础信息
     *
     * @param userId   司机用户编号
     * @param tenantId 车队id
     */
    List<VehicleDataInfo> getTenantVehicleRelByDriverUserIdObms(long userId, long tenantId);

    /**
     * 查询用户车辆车队关系数据
     *
     * @param userId 用户编号
     */
    List<TenantVehicleRel> getTenantVehicleRelByDriverUserId(long userId);

    /**
     * 将车队和车辆记录移入历史表
     *
     * @param relId 主键
     */
    void doRemoveVehicleV30(long relId, String token) throws Exception;


    /**
     * 查询车辆关系
     *
     * @param plateNumber
     * @param tenantId
     * @return
     * @throws Exception
     */
    TenantVehicleRel getTenantVehicleRel(String plateNumber, Long tenantId);

    /**
     * 查询租户下车辆列表信息
     *
     * @param type       类型 0-全部，1-自有车，3-外调车，6-挂车
     * @param tenantId   租户编号
     * @param vehicleNum 车牌号（模糊匹配）
     * @param orgId      部门编号  传0表示查询所有
     * @return List<Map> (vehicleNum 车牌号，orgId 机构编号，vehicleClass 车辆类型， linkMan 主驾，linkPhone 主驾电话)
     */
    List<Map> queryVehicleList(int type, long tenantId,
                               String vehicleNum, int orgId);

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
    List<Map> queryNoneOrderVehicle(Integer vehicleClass, Long tenantId,
                                    Long orgId, String plateNumber,
                                    int hour, int hourEnd, String isCount);

    /**
     * 更换车辆司机
     *
     * @param tenantId         租户编号
     * @param oriDriverUserId  车辆原有司机编号
     * @param destDriverUserId 车辆新的司机编号
     * @throws Exception
     */
    void changeVehicleDriver(Long tenantId, Long oriDriverUserId, Long destDriverUserId) throws Exception;

    /**
     * 实现功能: 分页查询我的车库
     *
     * @param plateNumber        车牌号码
     * @param linkman            司机
     * @param mobilePhone        司机手机
     * @param billReceiverMobile 车队手机
     * @param linkManName        车主联系人
     * @param linkPhone          车主手机
     * @param vehicleLength      车型(车长)
     * @param tenantName         车队名称
     * @param vehicleStatus      车型(类型)
     * @param startTime          注册时间开始
     * @param endTime            注册时间结束
     * @param authStateIn        认证状态
     * @param shareFlgIn         是否共享
     * @param isAuth
     * @param vehicleClass
     * @param vehicleGps         车辆类型
     * @param bdEffectDate       北斗购买车辆开始时间
     * @param bdInvalidDate      北斗购买车辆结束时间
     * @return
     */
    Page<TenantVehicleRelVo> doQueryVehicleAll(String plateNumber, String linkman, String mobilePhone, String billReceiverMobile, String linkManName,
                                               String linkPhone, String vehicleLength, String tenantName, Integer vehicleStatus, String startTime,
                                               String endTime, Integer authStateIn, Integer shareFlgIn, Integer isAuth, Integer vehicleClass, Integer vehicleGps,
                                               String bdEffectDate, String bdInvalidDate, String accessToken, Integer pageNum,
                                               Integer pageSize);

    /**
     * 实现功能: 分页查询我的车库
     *
     * @param plateNumber        车牌号码
     * @param linkman            司机
     * @param mobilePhone        司机手机
     * @param billReceiverMobile 车队手机
     * @param linkManName        车主联系人
     * @param linkPhone          车主手机
     * @param vehicleLength      车型(车长)
     * @param tenantName         车队名称
     * @param vehicleStatus      车型(类型)
     * @param startTime          注册时间开始
     * @param endTime            注册时间结束
     * @param authStateIn        认证状态
     * @param shareFlgIn         是否共享
     * @param isAuth
     * @param vehicleClass
     * @param vehicleGps         车辆类型
     * @param bdEffectDate       北斗购买车辆开始时间
     * @param bdInvalidDate      北斗购买车辆结束时间
     * @return
     */
    Page<TenantVehicleRelVo> doQueryVehicleAllIsOrder(Integer isWorking, String plateNumber, String linkman, String mobilePhone, String billReceiverMobile, String linkManName,
                                                      String linkPhone, String vehicleLength, String tenantName, Integer vehicleStatus, String startTime,
                                                      String endTime, Integer authStateIn, Integer shareFlgIn, Integer isAuth, Integer vehicleClass, Integer vehicleGps,
                                                      String bdEffectDate, String bdInvalidDate, String accessToken, Page<TenantVehicleRelVo> page, Long orgId);

    /**
     * 查询车队自由车车辆信息
     *
     * @param tenantId    车队id
     * @param vehicleCode 车辆编号
     */
    List<TenantVehicleRel> doQueryTenantVehicleRel(Long tenantId, Long vehicleCode);

    /**
     * 实现功能: 运行端查询列量列表
     *
     * @param startTime     注册开始时间
     * @param endTime       注册时间结束
     * @param plateNumber   车牌号码
     * @param linkman       司机
     * @param mobilePhone   司机手机
     * @param tenantName    车队名称
     * @param linkManName   车队联系人
     * @param linkPhone     车主手机
     * @param vehicleLength 车型(车长)
     * @param vehicleStatus 车型(类型)
     * @param authStateIn   认证状态
     * @param shareFlgIn    是否共享
     */
    PageInfo<VehicleDataVo> doQueryVehicleAllDis(Integer authStateIn, Integer shareFlgIn,
                                                 Integer isAuth, String startTime, String endTime, String plateNumber,
                                                 String linkManName, String linkPhone, String linkman, String mobilePhone,
                                                 String tenantName, String vehicleLength, Integer vehicleStatus, String accessToken,Integer  pageNum,Integer pageSize);


    /**
     * 清除一个司机作为副驾驶和随车司机的信息
     */
    public void clearCopilotDriverAndFollowDriver(Long driverUserId) throws Exception;


    /**
     * 删除车辆的平台归属记录
     *
     * @param plateNumber
     * @param vehicleCode
     * @throws Exception
     */
    public void removePtVehicle(String plateNumber, Long vehicleCode) throws Exception;

    /**
     * 查询车队车辆信息
     *
     * @param vehicleCode     车辆编号
     * @param beApplyTenantId 车队id
     */
    TenantVehicleRel getTenantVehicleRelByBeApplyTenantId(Long vehicleCode, Long beApplyTenantId);

    /**
     * 车辆信息导出
     */
    void acquireExcelFile(ImportOrExportRecords record, String plateNumber, String linkman, String mobilePhone,
                          String billReceiverMobile, String linkManName, String linkPhone, String vehicleLength,
                          String tenantName, Integer vehicleStatus, String startTime, String endTime, Integer authStateIn,
                          Integer shareFlgIn, Integer isAuth, Integer vehicleClass, Integer vehicleGps, String bdEffectDate,
                          String bdInvalidDate, String fieldName, String accessToken);

    /**
     * 查询车辆事发后存在车队
     *
     * @param vehicleCode  车辆编号
     * @param tenantId     车队id
     * @param vehicleClass 车辆类型
     */
    Long getZYCount(Long vehicleCode, Long tenantId, Integer vehicleClass);

    /**
     * 营运工作台 交强险预警数量
     */
    List<WorkbenchDto> getTableCompulsoryInsuranceCount();

    /**
     * 营运工作台 商业险预警数量
     */
    List<WorkbenchDto> getTableCommercialInsuranceCount();

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

    /**
     * 根据车牌号查询车辆的自有车关系
     *
     * @param plateNumber 车牌号
     */
    TenantVehicleRel getZYVehicleByPlateNumber(String plateNumber);

    /**
     * 获取车队和车两关系数据
     *
     * @param relIdList 主键id
     * @return
     */
    List<TenantVehicleRel> doQueryTenantVehicleRelByRelIds(List<Long> relIdList);

    /**
     * 接口编号：14521
     * 查询某个司机的所有自有车
     */
    List<TenantVehicleRel> getTenantVehicleRels(Long driverUserId, Integer vehicleClass);

    /**
     * 查询已认证司机车队车辆信息
     *
     * @param driverUserId 司机id
     * @param authState    认证状态
     */
    List<TenantVehicleRel> getTenantVehicleRelByDriverUserIdAndAuthState(Long driverUserId, Integer authState);

}
