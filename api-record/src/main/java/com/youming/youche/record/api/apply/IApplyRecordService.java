package com.youming.youche.record.api.apply;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.vo.VehicleApplyRecordVo;
import com.youming.youche.record.domain.apply.ApplyRecord;
import com.youming.youche.record.dto.ApplyRecordVehicleDto;
import com.youming.youche.record.dto.VehicleApplyRecordDto;
import com.youming.youche.record.vo.InvitationDataVo;
import com.youming.youche.record.vo.InvitationVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 申请记录表，会员跟车辆统一 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
public interface IApplyRecordService extends IBaseService<ApplyRecord> {

    /**
     * 条件查询申请记录
     *
     * @param driverUserId     业务主键，审核表主键id
     * @param applyTenantId    邀请租户ID
     * @param applyCarUserType 类型，申请会员类型
     * @param state            状态0 : 处理中  1 : 已通过 2：被驳回 3：平台仲裁中 4：平台仲裁通过 5：平台仲裁未通过
     * @return
     */
    List<ApplyRecord> getApplyRecordList(Long driverUserId, Long applyTenantId, Integer applyCarUserType, Integer state);

    /***
     * 我邀请的 和 邀请我的 数量接口
     * @return
     * @throws Exception
     */
    InvitationDataVo getInvitationCount(String accessToken);

    /***
     * 我邀请的 和 邀请我的 列表接口
     * @param recordType 0我邀请的   1邀请我的
     * @param plateNumber 车牌号码
     * @param tenantName 归属车队
     * @param linkPhone 车队手机
     * @param applyVehicleClass 申请车辆类型
     * @param state 状态0 : 处理中  1 : 已通过 2：被驳回 3：平台仲裁中 4：平台仲裁通过 5：平台仲裁未通过
     * @param driverMobile 司机手机
     * @param driverName 司机姓名
     * @return
     */
    Page<InvitationVo> doQueryVehicleApplyRecordAll(Integer recordType, Integer applyType, String plateNumber, String tenantName,
                                                    String linkPhone, Integer applyVehicleClass, Integer state, String driverMobile,
                                                    String driverName, Page<InvitationVo> page, String accessToken);

    /**
     * 接口编码：14511
     * 邀请信息审核-后台--车辆邀请列表--小程序接口
     */
    Page<InvitationVo> doQueryVehicleApplyRecordAllWX(Integer recordType, Integer applyType, String plateNumber, String tenantName,
                                                      String linkPhone, String applyVehicleClass, String state, String driverMobile,
                                                      String driverName, Integer pageNum, Integer pageSize, String accessToken);

    /**
     * 条件查询申请记录
     *
     * @param busiId        业务主键，审核表主键id
     * @param applyTenantId 邀请租户ID
     * @param state         状态0 : 处理中  1 : 已通过 2：被驳回 3：平台仲裁中 4：平台仲裁通过 5：平台仲裁未通过
     * @return
     */
    List<ApplyRecord> getApplyRecord(Long busiId, Long applyTenantId, int state);

    /**
     * 条件查询申请记录
     *
     * @param busiId           业务主键，审核表主键id
     * @param applyCarUserType 类型，申请会员类型
     * @param state            状态0 : 处理中  1 : 已通过 2：被驳回 3：平台仲裁中 4：平台仲裁通过 5：平台仲裁未通过
     * @return
     */
    List<ApplyRecord> getApplyRecord(Long busiId, Integer applyCarUserType, int state);

    /**
     * 修改申请记录状态为失效
     *
     * @param userId          业务主键，审核表主键id业务主键，审核表主键id
     * @param beApplyTenantId 被邀请租户ID
     */
    void invalidBeApplyRecord(Long userId, Long beApplyTenantId);


    /**
     * 方法实现说明  根据车辆id和审核状态查询，邀请记录
     *
     * @param vehicleCode 车辆id
     * @param state
     * @return java.util.List<com.youming.youche.record.domain.apply.ApplyRecord>
     * @throws
     * @author terry
     * @date 2022/3/18 18:15
     */
    List<ApplyRecord> selectByBusidAndState(long vehicleCode, int state);

    /**
     * 条件查询申请记录
     *
     * @param userId   业务主键，审核表主键id业务主键，审核表主键id
     * @param state    状态0 : 处理中  1 : 已通过 2：被驳回 3：平台仲裁中 4：平台仲裁通过 5：平台仲裁未通过
     * @param tenantId 车队id
     * @return
     */
    List<ApplyRecord> getApplyRecordList(Long userId, int state, Long tenantId);

    /**
     * 查询申请记录
     * @param vehicleCode 业务主键，审核表主键id业务主键，审核表主键id
     * @param tenantId 邀请租户ID
     * @return
     */
    Map<String, Object> getVehicleRecordInfo(long vehicleCode, Long tenantId);

    /**
     * 查询邀请详细信息
     *
     * @param applyRecordId 邀请主键id
     * @return
     */
    VehicleApplyRecordVo getVehicleApplyRecord(Long applyRecordId);

    /**
     * 查询符合条件的邀请记录数
     *
     * @param tenantId     邀请租户ID
     * @param vehicleClass 类型，申请车辆类型
     * @param vehicleCode  业务主键
     * @return
     */
    Long getApplyCount(Long tenantId, Integer vehicleClass, Long vehicleCode);

    /**
     * 查询符合条件的车辆邀请记录数
     *
     * @param tenantId     邀请租户ID
     * @param vehicleClass 类型，申请车辆类型
     * @param driverUserId 司机用户编号
     * @return
     */
    Long getApplyVehicleCountByDriverUserId(Long tenantId, Integer vehicleClass, Long driverUserId);

    /**
     * 保存邀请记录
     * @param applyRecord
     * @return
     */
    ApplyRecord saveApplyRecord(ApplyRecord applyRecord);

    /**
     * 14512
     * 查看车辆邀请详情-小程序接口
     */
    VehicleApplyRecordDto getVehicleApplyRecordForMiniProgram(Long applyRecordId, String accessToken);

    /**
     * 查询处理中的被邀请人记录
     */
    Long queryApplyCount(String accessToken);

    /**
     * 接口编号：14000
     * 接口入参：
     *        objId	         申请编号
     * 接口出参：
     *       busiId  主键id，处理邀请时回调用
     *       tenantName	          申请车队
     *       tenantLinkPhone	      车队电话
     *       plateNumber          转移车辆  applyType为2的时候才有值
     *       createDate	         申请时间	 	格式：2015-05-06
     *       applyTypeName       申请类型
     *       applyRemark         申请说明
     *       applyFileUrl            申请附件,小图
     *       state              状态0 : 处理中  1 : 已通过 2：被驳回 3：平台仲裁中 4：平台仲裁通过 5：平台仲裁未通过  处理中的需要可以审核
     *       stateName              状态中文
     *       applyType          类型，1 司机 2 车辆  司机车辆最后调用的审核方法不一样
     *       vehicles[           车辆列表 applyType为1的时候，需要处理
     *                  plateNumber   车牌
     *                  vehicleTypeString   车辆类型
     *                  checked      查看详情的时候该车是否已经被选中
     *              ]
     *
     * 查询申请详情包括司机和车辆
     */
    ApplyRecordVehicleDto getApplyRecord(Long applyId);

    /**
     * 查询消息信息
     */
    List<ApplyRecord> getUpdateMessageFlagAllApplyRecordData(Long id, Integer state);

}
