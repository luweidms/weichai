package com.youming.youche.system.api.apply;//package com.youming.youche.system.api.apply;
//
//
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.youming.youche.commons.base.IBaseService;
//import com.youming.youche.commons.vo.VehicleApplyRecordVo;
//import com.youming.youche.system.domain.apply.ApplyRecord;
//import com.youming.youche.system.dto.VehicleApplyRecordDto;
//import com.youming.youche.system.vo.InvitationDataVo;
//import com.youming.youche.system.vo.InvitationVo;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * <p>
// * 申请记录表，会员跟车辆统一 服务类
// * </p>
// *
// * @author Terry
// * @since 2021-11-19
// */
//public interface IApplyRecordService extends IBaseService<ApplyRecord> {
//
//
//    List<ApplyRecord> getApplyRecordList(Long driverUserId, Long applyTenantId, Integer applyCarUserType, Integer state);
//
//    /***
//     * 我邀请的 和 邀请我的 数量接口
//     * @return
//     * @throws Exception
//     */
//    InvitationDataVo getInvitationCount(String accessToken);
//
//    /***
//     * 我邀请的 和 邀请我的 列表接口
//     * @param recordType 0我邀请的   1邀请我的
//     * @param plateNumber 车牌号码
//     * @param tenantName 归属车队
//     * @param linkPhone 车队手机
//     * @param applyVehicleClass 申请车辆类型
//     * @param state 状态0 : 处理中  1 : 已通过 2：被驳回 3：平台仲裁中 4：平台仲裁通过 5：平台仲裁未通过
//     * @param driverMobile 司机手机
//     * @param driverName 司机姓名
//     * @return
//     */
//    Page<InvitationVo> doQueryVehicleApplyRecordAll(Integer recordType, Integer applyType, String plateNumber, String tenantName,
//                                                    String linkPhone, Integer applyVehicleClass, Integer state, String driverMobile,
//                                                    String driverName, Page<InvitationVo> page, String accessToken);
//
//
//    List<ApplyRecord> getApplyRecord(Long busiId, Long applyTenantId, int state);
//
//    List<ApplyRecord> getApplyRecord(Long busiId, Integer applyCarUserType, int state);
//
//    void invalidBeApplyRecord(Long userId, Long beApplyTenantId);
//
//
//    /**
//     * 方法实现说明  根据车辆id和审核状态查询，邀请记录
//     *
//     * @param vehicleCode 车辆id
//     * @param state
//     * @return java.util.List<com.youming.youche.record.domain.apply.ApplyRecord>
//     * @throws
//     * @author terry
//     * @date 2022/3/18 18:15
//     */
//    List<ApplyRecord> selectByBusidAndState(long vehicleCode, int state);
//
//    List<ApplyRecord> getApplyRecordList(Long userId, int state, Long tenantId);
//
//    Map<String, Object> getVehicleRecordInfo(long vehicleCode, Long tenantId);
//
//    VehicleApplyRecordVo getVehicleApplyRecord(Long applyRecordId);
//
//    Long getApplyCount(Long tenantId, Integer vehicleClass, Long vehicleCode);
//
//    Long getApplyVehicleCountByDriverUserId(Long tenantId, Integer vehicleClass, Long driverUserId);
//
//
//    ApplyRecord saveApplyRecord(ApplyRecord applyRecord);
//
//    VehicleApplyRecordDto getVehicleApplyRecordForMiniProgram(Long applyRecordId);
//
//}
