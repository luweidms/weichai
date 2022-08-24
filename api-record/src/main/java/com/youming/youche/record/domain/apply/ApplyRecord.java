package com.youming.youche.record.domain.apply;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 申请记录表，会员跟车辆统一
 * </p>
 *
 * @author Terry
 * @since 2021-12-31
 */
@Data
public class ApplyRecord extends BaseDomain {

    /**
     * 类型，1 会员 2 车辆
     */
    private Integer applyType;

    /**
     * 类型，申请会员类型
     */
    private Integer applyCarUserType;

    /**
     * 类型，申请车辆类型
     */
    private Integer applyVehicleClass;

    /**
     * 业务主键，审核表主键id
     */
    private Long busiId;

    /**
     * 邀请说明
     */
    private String applyRemark;

    /**
     * 申请附件
     */
    private Long applyFileId;

    /**
     * 邀请租户ID
     */
    private Long applyTenantId;

    /**
     * 被邀请租户ID
     */
    private Long beApplyTenantId;

    /**
     * 操作人,对应sys_operator的operator_id
     */
    private Long opId;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 审核操作人,对应sys_operator的operator_id
     */
    private Long auditOpId;

    /**
     * 审核时间
     */
    private LocalDateTime auditDate;

    /**
     * 审核备注
     */
    private String auditRemark;

    /**
     * 新邀请记录ID
     */
    private Long newApplyId;

    /**
     * 状态0 : 处理中  1 : 已通过 2：被驳回 3：平台仲裁中 4：平台仲裁通过 5：平台仲裁未通过
     */
    private Integer state;

    /**
     * 申请转移自有车接收司机id
     */
    private Long applyDriverUserId;

    /**
     * 行驶证图片id
     */
    private String drivingLicense;

    /**
     * 运营证图片id
     */
    private String operCerti;

    /**
     * 操作类型 1 新增 2修改
     */
    private Integer busiCode;

    /**
     * 申请转移接收的车牌，逗号隔开
     */
    private String applyPlateNumbers;

    /**
     * 阅读状态，0未读 1已读
     */
    private Integer readState;

    /**
     * 历史id
     */
    private Long hisId;

    /**
     * 账单接收人手机号
     */
    private String billReceiverMobile;

    /**
     * 账单接收人用户编号
     */
    private Long billReceiverUserId;

    /**
     * 账单接收人姓名
     */
    private String billReceiverName;

    /**
     * 车辆归属司机或车辆归属车队的绑定司机
     */
    private Long belongDriverUserId;

    /**
     * 车辆类型
     */
    @TableField(exist = false)
    private Boolean isModifyVehicleClass;


}
