package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Date:2021/12/24
 */
@Data
public class InvitationVo implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * id
     */
    private Long eid;

    /**
     * 业务主键，审核表主键id
     */
    private Long vehicleCode;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 申请车辆类型
     */
    private Integer applyVehicleClass;

    /**
     * 申请车辆类型
     */
    private String applyVehicleClassName;

    /**
     * 被邀请租户ID
     */
    private Long beApplyTenantId;

    /**
     * 邀请租户ID
     */
    private Long applyTenantId;

    /**
     * 归属/邀请车队名称
     */
    private String tenantName;

    /**
     * 车队联系手机
     */
    private String linkPhone;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 邀请说明
     */
    private String applyRemark;

    /**
     * 状态0 : 处理中  1 : 已通过 2：被驳回 3：平台仲裁中 4：平台仲裁通过 5：平台仲裁未通过
     */
    private Integer state;

    private String stateName;

    /**
     * 阅读状态，0未读 1已读
     */
    private Integer readState;

    /**
     * 审核备注
     */
    private String auditRemark;

    private Integer verState;

    /**
     * 新邀请记录ID
     */
    private Long newApplyId;

    /**
     * 联系人手机
     */
    private String beApplyDriverMobile;

    /**
     * 联系人姓名
     */
    private String beApplyDriverName;

    /**
     * 审核时间
     */
    private String auditDate;

    private Integer readFlg;


    /**
     * 审核（0不显示审核、1显示审核）
     */
    private Integer audit;

    /**
     * 邀请种类
     */
    private Integer applyCarUserType;
    private String applyCarUserTypeName;

    /**
     * 类型，1 司机 2 车辆
     */
    private Integer applyType;

}
