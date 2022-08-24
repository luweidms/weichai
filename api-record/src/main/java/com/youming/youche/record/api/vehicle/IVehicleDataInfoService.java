package com.youming.youche.record.api.vehicle;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.dto.AuditDto;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.vo.VehicleApplyRecordVo;
import com.youming.youche.record.domain.tenant.TenantVehicleRel;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.record.dto.vehicle.VehicleReportVehicleDataDto;
import com.youming.youche.record.domain.vehicle.VehicleStaticData;
import com.youming.youche.record.dto.*;
import com.youming.youche.record.vo.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 车辆表 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@Service
public interface IVehicleDataInfoService extends IBaseService<VehicleDataInfo> {

    /**
     * 车牌号精确查询车辆信息
     *
     * @param plateNumber 车牌号
     * @return
     */
    VehicleDataInfo getVehicleDataInfo(String plateNumber);

    /**
     * 根据 车辆id 和租户id查询
     *
     * @param vehicleCode
     * @param tenantId
     * @return
     */
    VehicleDataInfo getVehicleData(Long vehicleCode, Long tenantId);

    /**
     * 查询车队车辆信息
     *
     * @param plateNumber 车牌号
     * @param tenantId    车队id
     * @return
     */
    VehicleDataInfo getVehicle(String plateNumber, Long tenantId);

    /**
     * 模糊查询车辆信息
     *
     * @param plateNumber 车牌号
     * @param pageNum     分页参数
     * @param pageSize    分页参数
     */
    Page<VehicleDataInfoiVo> getVehicleDataInfoPlateNumber(Integer pageNum, Integer pageSize, String plateNumber);

    /**
     * 车辆id查询车辆信息
     *
     * @param vehicleCode 车帘编号
     */
    VehicleDataInfo getVehicleDataInfo(Long vehicleCode);

    /**
     * 实现功能: 启动闲置
     *
     * @param flag 1，闲置、 其他，启动
     * @param vid  挂车id
     */
    Boolean updateVehicleDataInfo(Short flag, Long vid, String plateNumber, String accessToken);


    /**
     * 快速创建车量
     *
     * @return
     * @throws Exception
     */

    String doSaveQuickVehicle(long tenantId, String plateNumber, long driverUserId, int vehicleStatus, String vehicleLength, int licenceType) throws Exception;

    /***
     * 根据车辆id(vehicleCode)查询信息
     *入参   long vehicleCode
     * 出参：
     * ************车辆租户关系表******************
     *  plateNumber   车牌号码
     *  loadEmptyOilCost                空载油耗
     *   loadFullOilCost               载重油耗
     *   isUseCarOilCost               使用车辆油耗
     *  vehicleClass  车辆类型
     *  vehicleClassName  车辆类型
     *   *  orgId   归属部门
     *   *  orgName   归属部门
     *  userId  归属人
     *  userName  归属人
     *  shareFlg        是否共享（0否 1是）
     *  linkUserId      共享联系人id
     *  shareUserName      共享联系人
     *  shareMobilePhone      共享联系人电话
     *  vehiclePicture   车辆图片ID
     *  drivingLicense   行驶证正本ID
     *  adriverLicenseCopy   行驶证副本ID
     *  operCertiId   运输证ID
     *  relId           主键id
     *  ***********车辆主表*******************
     *  *   driverUserId   司机ID
     *  *   driverUserName   司机姓名
     *  *   driverMobilePhone   司机联系手机号
     *  *   driverCarUserType   司机类型
     *  *   driverCarUserTypeName   司机类型
     *  ----自有车才有-----------
     *  copilotDriverId   副驾驶ID
     *  copilotDriverUserName   副驾驶姓名
     *  copilotDriverMobilePhone   副驾驶联系手机
     *  copilotDriverUserType   副驾驶类型
     *  copilotDriverUserTypeName   副驾驶类型
     *  followDriverId   随车司机ID
     *  followDriverUserName   随车司机姓名
     *  followDriverMobilePhone   随车司机联系手机号
     *  followDriverUserType   随车司机类型
     *  followDriverUserTypeName   随车司机类型
     *    vehicleValidityTime          行驶证有效期
     *    operateValidityTime          运营证有效期
     *
     *    vehiclePicture    车辆图片id
     *    drivingLicense    行驶证正本id
     *    adriverLicenseCopy    行驶证副本id
     *    ----自有车才有-----------
     *
     *  ----  车辆归属自有车车队信息  外调车才有  ----
     *    tenantName    车队名称
     *    tenantLinkMan 负责人
     *    tenantLinkPhone   联系电话
     *
     *    orgName   归属组织
     *    orgId  归属组织
     *    userName  归属人
     *    userId     归属人
     *    ----  车辆归属自有车车队信息  外调车才有  ----
     *  licenceType   牌照类型(1:整车，2：拖头)
     *  licenceTypeName   牌照类型(1:整车，2：拖头)
     *  vehicleLength  车长(车型)
     *  vehicleLengthName  车长(车型)
     *  vehicleStatus  类型(车型)
     *  vehicleStatusName  类型(车型)
     *  vehicleLoad    载重(吨)
     *  lightGoodsSquare 容积（方）
     *  vinNo         车架号
     *  operCerti       运营证号
     *  engineNo        发动机号
     *  drivingLicenseSn 行驶证号
     *  brandModel       车辆品牌
     *  etcCardNumber                ETC卡号
     *  equipmentCode                GPS设备号
     vehicleCode           主键id
     *  租户车辆成本关系表
     *  price  价格
     *  loanInterest        贷款利息
     *  interestPeriods        还款期数
     *  payInterestPeriods        已还期数
     *  purchaseDate        购买时间
     *  depreciatedMonth        折旧月数
     *  collectionInsurance        保险
     *  examVehicleFee        审车费
     *  maintainFee        保养费
     *  repairFee        维修费
     *   tyreFee       轮胎费
     *   otherFee       其他
     *   tenantVehicleCostRelId 主键id
     *   证照信息表
     *   annualVeriTime           年审时间
     *   seasonalVeriTime           季审时间
     *   insuranceTime           保险时间
     *   insuranceCode           保险单号
     *    maintainDis          保养周期
     *    maintainWarnDis         保养预警公里
     *   prevMaintainTime           上次保养时间
     *   registrationTime           登记日期
     *   registrationNumble           登记证号
     *   tenantVehicleCertRelId   主键id
     *   心愿线路表  vehicleOjbectLineArray
     *   sourceProvince     出发省id
     *   sourceRegion     出发市id
     *   sourceCounty     出发区id
     *   desProvince     目的省id
     *   desRegion     目的市id
     *   desCounty     目的区id
     *
     *   *   sourceProvinceName     出发省
     *   sourceRegionName     出发市
     *   sourceCountyName     出发区
     *   desProvinceName     目的省
     *   desRegionName     目的市
     *   desCountyName     目的区
     *
     *   carriagePrice     价格
     *   id    主键
     */
    VehicleInfoDto getAllVehicleInfo(Integer vehicleClass, Long vehicleCode, String plateNumbers, String accessToken);

    /**
     * 被邀请 车辆详情 查询历史
     *
     * @param vehicleClass
     * @param vehicleCode
     * @param plateNumbers
     * @param accessToken
     * @return
     */
    VehicleInfoDto getAllVehicleInfoVerHis(Integer vehicleClass, Long vehicleCode, String plateNumbers, String accessToken);

    /***
     * 修改车辆(自有车，招商车，外调车)
     * ************车辆租户关系表******************
     *  plateNumber   车牌号码
     *  loadEmptyOilCost                空载油耗
     *   loadFullOilCost               载重油耗
     *   isUseCarOilCost               使用车辆油耗
     *  vehicleClass  车辆类型
     *  orgId   归属部门
     *  userId  归属人
     *  shareFlg        是否共享（0否 1是）
     *  vehiclePicture   车辆图片ID
     *  drivingLicense   行驶证正本ID
     *  adriverLicenseCopy   行驶证副本ID
     *  linkUserId  联系人id
     *  ***********车辆主表*******************
     *  *   driverUserId   司机ID
     *  copilotDriverId   副驾驶ID
     *  followDriverId   随车司机ID
     *    vehicleValidityTime          行驶证有效期
     *    operateValidityTime          运营证有效期
     *  licenceType   牌照类型(1:整车，2：拖头)
     *  vehicleLength  车长(车型)
     *  vehicleStatus  类型(车型)
     *  vehicleLoad    载重(吨)
     *  lightGoodsSquare 容积（方）
     *  vinNo         车架号
     *  operCerti       运营证号
     *  engineNo        发动机号
     *  drivingLicenseSn 行驶证号
     *  etcCardNumber                ETC卡号
     *  equipmentCode                GPS设备号
     *  租户车辆成本关系表
     *  price  价格
     *  loanInterest        贷款利息
     *  interestPeriods        还款期数
     *  payInterestPeriods        已还期数
     *  purchaseDate        购买时间
     *  depreciatedMonth        折旧月数
     *  collectionInsurance        保险
     *  examVehicleFee        审车费
     *  maintainFee        保养费
     *  repairFee        维修费
     *   tyreFee       轮胎费
     *   otherFee       其他
     *   证照信息表
     *   annualVeriTime           年审时间
     *   seasonalVeriTime           季审时间
     *   insuranceTime           保险时间
     *   insuranceCode           保险单号
     *    maintainDis          保养周期
     *    maintainWarnDis         保养预警公里
     *   prevMaintainTime           上次保养时间
     *   registrationTime           登记日期
     *   registrationNumble           登记证号
     *   心愿线路表  vehicleOjbectLineArray
     *   sourceProvince     出发省id
     *   sourceRegion     出发市id
     *   sourceCounty     出发区id
     *   desProvince     目的省id
     *   desRegion     目的市id
     *   desCounty     目的区id
     *   carriagePrice     价格
     * @return Y--业务处理成功  N-业务处理失败
     */
    boolean doUpdateVehicleInfo(VehicleInfoUptVo vehicleInfoUptVo, String accessToken) throws BusinessException;

    /***
     * 新增车辆
     *  @return Y--业务处理成功  N-业务处理失败
     */
    AuditDto doSaveVehicleInfo(VehicleInfoVo vehicleInfoVo, String accessToken) throws Exception;

    /**
     * 实现功能：车辆导入
     */
    String deal(byte[] bytes, ImportOrExportRecords records, String token);

    /**
     * 实现功能: 删除车辆
     *
     * @param vehicleCode 车牌号
     * @return
     */
    boolean doRemoveVehicle(Long vehicleCode, Boolean delOutVehicleDriver, String accessToken);

    /***
     * 车辆历史档案列表
     * @param   plateNumber  车牌号码
     * @param   linkman  司机
     * @param   mobilePhone  司机手机
     * @param   linkManName  车主联系人
     * @param   linkPhone  车主手机
     * @param   vehicleClass  车辆类型
     * @param   vehicleLength  车型(车长)
     * @param   vehicleStatus  车型(类型)
     *
     * @return hisId   主键id
     * @return vehicleCode   车辆编号(车辆表主键)
     * @return plateNumber   车牌号码
     * @return licenceType   牌照类型
     * @return licenceTypeName   牌照类型
     * @return vehicleLength   车型(车长)
     * @return vehicleStatus   车型(车辆类型)
     * @return vehicleClass   车辆类型
     * @return vehicleClassName   车辆类型
     * @return linkman   司机
     * @return mobilePhone   司机手机
     * @return tenantId   归属车队
     * @return tenantName   归属车队名称
     * @return linkPhone   车队手机
     * @throws Exception
     */
    Page<HistoricalArchivesVo> doQueryVehicleAllHis(String plateNumber, String linkManName, String tenantName, String linkPhone,
                                                    String linkman, String mobilePhone, String vehicleLength, Integer vehicleStatus,
                                                    Integer vehicleClass, Page<HistoricalArchivesVo> page, String accessToken);

    /**
     * 修改车辆信息状态（0不可用 1可用 9被移除）
     *
     * @param tableName 表
     * @param inMap     vehicleCode 车联编号
     *                  tenantId 车队id
     *                  destVerState 状态（0不可用 1可用 9被移除）
     * @throws BusinessException
     */
    void updateVehicleVerAllVerState(String tableName, Map<String, Object> inMap) throws BusinessException;

    /**
     * 实现功能:查询邀请我的车辆详情
     *
     * @param vehicleCode 车辆id
     * @return
     */
    VehicleDataInfoVo getShareVehicle(Long vehicleCode, String accessToken);


    /**
     * 实现功能: 查询车辆历史记录详情
     *
     * @param vehicleCode  车辆id
     * @param verState     1查可审核数据  9查历史数据
     * @param plateNumbers 车牌
     * @return
     */
    Map<String, Object> getAllVehicleInfoVer(Long vehicleCode, Integer verState, String plateNumbers, String accessToken);

    /**
     * 实现功能: 审核车辆
     *
     * @param busiCode     业务编码
     * @param busiId       业务主键id
     * @param desc         审核描述
     * @param chooseResult 1 审核通过 2 审核不通过
     * @param instId       通过这个判断当前待审核的数据是否跟数据库一致
     */
    Boolean sure(String busiCode, Long busiId, String desc, Integer chooseResult, Long instId, String accessToken);

    /**
     * 获取车辆车型
     */
    List<VehicleStaticData> getVehicleStatusWithLength(Boolean orderFlag);

    /**
     * 查询用户车帘信息
     *
     * @param userId 用户编号
     */
    List<VehicleDataInfo> getDriverAllRelVehicleList(Long userId);

    /**
     * 实现功能: 获取车辆的注册信息
     *
     * @param plateNumber    车牌号
     * @param vehicleClassIn 车辆类型
     */
    Map<String, Object> getVehicleIsRegister(String plateNumber, Integer vehicleClassIn, String accessToken);

    /**
     * 实现功能: 获取车辆的注册信息
     *
     * @param plateNumber    车牌号
     * @param vehicleClassIn 车辆类型
     */
    Map<String, Object> getVehicleIsRegisterDatainfo(String plateNumber, Integer vehicleClassIn, String accessToken);

    /**
     * 实现功能: 邀请列表查询操作详情
     *
     * @param applyRecordId 申请记录主键id
     */
    VehicleApplyRecordVo getVehicleInviteRecord(Long applyRecordId);

    /**
     * 再次申请转移
     *
     * @param applyRecorDto
     * @param accessToken
     * @return
     */
    boolean doSaveApplyRecord(ApplyRecorDto applyRecorDto, String accessToken);

    /**
     * 删除车辆
     *
     * @param Id 车辆编号、
     */
    Long remove(Long id, String accessToken);

    /**
     * 流程结束，审核通过的回调方法
     *
     * @param busiId 业务的主键
     * @param desc   结果的描述
     * @return
     */
    public void sucess(Long busiId, String desc, Map paramsMap, String token) throws BusinessException;

    /**
     * 流程结束，审核不通过的回调方法
     *
     * @param busiId 业务的主键
     * @param desc   结果的描述
     * @return
     */
    public void fail(Long busiId, String desc, Map paramsMap, String token) throws BusinessException;

    /**
     * 删除自有车删除所有的绑定关系
     *
     * @param busiId 车辆编号
     * @param token
     */
    void removeOwnCarAndNotifyOtherTenant(long busiId, String token);

    /**
     * 清除邀请
     *
     * @param vehicleCode 车辆编号
     */
    void removeApplyRecord(long vehicleCode);

    /**
     * 删除车辆
     */
    void dissolveCooperationVehicle(long relId, long vehicleDataInfoVerHisId, String accessToken) throws BusinessException;

    /**
     * /删除司机关系
     *
     * @param userId  司机编号
     * @param tenanId 车队id
     */
    void doRemoveDriverOnlyOneVehicle(long userId, long tenanId);

    /**
     * 已注册 自有车 保存信息(车辆档案私人库使用)
     *
     * @return
     * @throws Exception
     */
    Boolean doSaveApplyRecordForOwnCar(ApplyRecorDto applyRecorDto, String accessToken);

    /***
     * 共享车库列表
     * @param vehicleDataInfoiDto
     * @return
     * @throws Exception
     */
    Page<VehicleDataInfoiVo> doQueryAllShareVehicle(VehicleDataInfoiDto vehicleDataInfoiDto, Integer pageNum, Integer pageSize);

    /**
     * 实现功能: 获取车辆相关联的司机
     *
     * @param plateNumber 车牌编号
     */
    List<VehicleDriverVo> getVehicleDriver(String plateNumber);

    /**
     * 实现功能: 运营端查询挂靠人信息
     *
     * @param mobile 手机号
     * @param flag   1-招商车，2-挂靠车
     */
    UserDataInfoBackVo queryBillReceiverName(String mobile, int flag, String token);

    /**
     * 更新车量完整性
     *
     * @param vehicleCode 车辆编号
     */
    public void doUpdateVehicleCompleteness(long vehicleCode, Long tenantId);

    /**
     * 把某个司机在某个车队的所有车转移到目标车队
     */
    public void doTransferOwnCar(Long driverUserId, Long origTenantId, Long destTenantId, String token) throws Exception;

    /**
     * 实现功能: 新增运营车辆
     *
     * @param obmsVehicleDataVo 新增运营车辆信息
     */
    boolean save(OBMSVehicleDataVo obmsVehicleDataVo, String accessToken);

    /**
     * 实现功能: 修改运营车辆
     *
     * @param obmsVehicleDataVo 运营车辆信息
     */
    boolean update(OBMSVehicleDataVo obmsVehicleDataVo, String accessToken);

    /**
     * 实现功能: 获取运营车辆详情
     *
     * @param vehicleCode 车牌编号
     * @return 运营车辆详情
     */
    OBMSVehicleInfoDto obmsGet(Long vehicleCode, String accessToken);

    /**
     * 添加车队车辆关系
     *
     * @param vehicleCode 车辆编号
     */
    public void addPtTenantVehicleRel(Long vehicleCode);

    /**
     * 实现功能: 邀请列表查询操作详情
     *
     * @param vehicleCode 车辆编号
     * @return 心愿线路信息
     */
    List<VehicleObjectLineVo> getVehicleObjectLines(Long vehicleCode);

    /**
     * 获取当前部门下的所有车辆
     *
     * @param id 部门id
     */
    List<TenantVehicleRel> getVehicleByOrganize(Long id);


    /***
     * 根据司机id  车牌号码plateNumbers 查询司机名下是否存在其他车
     * @param driverUserId 司机ID
     * @param plateNumber 车牌号码
     * @return
     * @throws Exception
     */
    Boolean isBindOtherVehicle(Long driverUserId, String plateNumber, LoginInfo user);


    /**
     * 根据手机号码查询最新的位置，需要判断对应的车是否有接入G7，易流的设备
     *
     * @param billId     手机
     * @param platNumber 车牌号码
     * @return 纬度，经度，时间
     */
    String[] getPositionByBillId(String billId, String platNumber);

    /**
     * 车讯车辆证件信息
     *
     * @param plateNumber 车牌号
     */
    VehicleCertInfoDto getTenantVehicleCertRel(String plateNumber);


    /**
     * 流程结束，审核通过的回调方法
     *
     * @param busiId 业务的主键
     * @param desc   结果的描述
     * @return
     */
    void sucessVehicle(Long busiId, String desc, Map paramsMap, LoginInfo user, String tokeng);

    /**
     * 审核操作：现金打款的特殊的流程处理：
     * 在最后的一个节点的审核，调用该方法，触发审核的回调但是不修改审核的数据，还是保持当前的状态。
     * 由其他地方再调用一次这个方法，传入不同的类型，触发审核流程的真正完结，这个时候是不需要触发回调方法。
     *
     * @param busiCode     业务编码
     * @param busiId       业务主键id
     * @param desc         审核描述
     * @param chooseResult 1 审核通过 2 审核不通过
     * @param instId       通过这个判断当前待审核的数据是否跟数据库一致
     * @param type         1:只是触发流程的回调函数 ，不对审核的数据做任何处理
     *                     2 不触发流程的回调函数，处理审核的数据
     * @param tenantId     租户id,task没有办法通过session去取，需要传入进来
     * @throws Exception true 有下个节点
     *                   false 流程结束
     */
    Boolean sure(String busiCode, Long busiId, String desc,
                 Integer chooseResult, Long instId, Integer type, Long tenantId, String accessToken);

    /**
     * 更新车辆的定位设备信息，优先级：北斗 > G7 > 易流 > app
     *
     * @param plateNumber   车牌
     * @param equipmentCode 设备号
     * @param equipmentType 定位类型
     */
    Integer updateEquipmentCode(String plateNumber, String equipmentCode, int equipmentType);

    /**
     * 更新车辆的定位设备信息，优先级：北斗 > G7 > 易流 > app
     *
     * @param plateNumber   车牌
     * @param equipmentCode 设备号
     * @param equipmentType 定位类型，传0表示重置定位设备信息，设备号及定位类型都设为空
     * @param operatorType  定位类型传0时，必须传入operatorType表示要请掉的类型，只有操作类型与车辆中的定位类型相同时，重置操作才会生效，否则忽略
     */
    Integer updateEquipmentCode(String plateNumber, String equipmentCode, int equipmentType, Integer operatorType);


    /***
     * 接口编码：40012
     * 查询车辆信息
     */
    VehicleDataInfoVxDto getVehicleByPlateNumberVx(String plateNumber);

    /**
     * 检查车量资料完整性
     *
     * @param plateNumber 车辆编号
     * @param goodsType   货物类型，1-普货，2-危险品
     * @return
     */
    boolean checkVehicleCompleteNess(String plateNumber, int goodsType);

    /**
     * 通过车牌号码查询车辆信息列表
     *
     * @param plateNumber 车牌号码
     * @param tenantId    车队id
     */
    List<VehicleDataInfoVxVo> getVehicle(String plateNumber, long tenantId, boolean isSelf);


    /**
     * 订单专用
     *
     * @param loginAcct 账号
     */
    List<DriverDataInfoDto> doQueryCarDriver(String loginAcct, Long tenantId);


    /**
     * 根据userId获取员工信息
     */
    StaffDataInfoDto getStaffInfoByUserId(Long userId, Long TenantId);

    /**
     * 14510
     * 在线接单-通过车牌号码查询车辆信息列表--微信接口
     */
    Page<VehicleDataInfoVxVo> getVehiclePage(Page<VehicleDataInfoVxVo> page, String plateNumber, String accessToken);

    /**
     * 我的视图-后台--我的车辆
     *
     * @param driverUserId 司机id
     * @return authState 状态 authStateName 状态中文
     */
    VehicleCountDto doQueryVehicleCountByDriver(Long driverUserId);

    /**
     * 查询用户拥有车辆数
     *
     * @param driverUserId 司机id
     */
    Long getVehicleCountByDriverUserId(Long driverUserId);

    /**
     * 14500
     * 新增车辆--APP
     */
    void doSaveVehicle(SaveVehicleAppVo vo, String accessToken);

    /**
     * 判断车辆是否存在
     *
     * @param plateNumber 车牌号码
     */
    Long getVehicleDataInfoByPlateNumber(String plateNumber);

    /**
     * 14503 APP合作车队列表
     *
     * @param vehicleCode  车辆编码
     * @param driverUserId 司机编码
     */
    List<QueryTenantDto> doQueryTenantByVehicleCodeApp(Long vehicleCode, Long driverUserId);

    /**
     * 查询C端车，在各个车队的订单的数量：在途+已经完成的订单
     *
     * @param vehicleCode
     * @return key:各个车队的租户id value:对应在各个车队下的订单数量
     */
    Map<Long, Long> getOrderCountByVehicleCode(Long vehicleCode);

    /**
     * 查询自由车在归属的车队下的订单数量：在途+已经完成的订单
     *
     * @param vehicleCode 车辆id
     * @param tenantId    归属的车队
     */
    Long getOrderCountByVehicleCode(Long vehicleCode, Long tenantId);

    /**
     * 14501
     * 修改车辆--APP
     */
    void doUpdateVehicle(UpdateVehicleVo vo, String accessToken);

    /**
     * 14502
     * 删除车辆
     *
     * @param vehicleCodeString vehicleCodes 车辆编码(多个，逗号分隔)
     */
    void doRemoveVehicleWx(String vehicleCodeString, String accessToken);

    /**
     * 14504
     * 退出车队--APP relIds 车辆车队关系id (多个，逗号分隔)
     */
    void doQuitTenant(String relIds, String accessToken);

    /**
     * 14505
     * 查看车辆--APP
     *
     * @param vehicleCode 车辆编码
     */
    VehicleInfoAppDto getVehicleInfoApp(Long vehicleCode);

    /**
     * 14506
     * 我的车辆列表
     */
    List<QueryVehicleByDriverDto> doQueryVehicleByDriver(Long driverUserId);

    /**
     * 修改车辆维保里程、最后维保时间
     *
     * @param plateNumber         车牌号
     * @param maintenanceDis      维保里程
     * @param lastMaintenanceDate 最后维保时间
     */
    void updateMaintenance(String plateNumber, Integer maintenanceDis, LocalDateTime lastMaintenanceDate);

    /**
     * 车辆id获取车辆基础信息
     */
    List<VehicleDataInfo> getCarTenantId(Long tenantId);

    /**
     * 获取车辆报表所需车辆数据
     */
    List<VehicleReportVehicleDataDto> getVehicleDataAll();

    /**
     * 车帘费用获取车辆保险、车辆折旧费
     */
    TenantVehicleRelInfoDto getVehicleInsuranceFee(Integer vehicleClass, Long vehicleCode, Long tenantId);

}
