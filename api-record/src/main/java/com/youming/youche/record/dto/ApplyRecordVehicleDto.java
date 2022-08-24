package com.youming.youche.record.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ApplyRecordVehicleDto implements Serializable {
    private static final long serialVersionUID = -3511017467020840986L;

    private Long busiId; // 业务主键
    private String tenantName; // 车队名称
    private String tenantLinkPhone; // 车队手机号
    private String createDate; // 创建时间
    private Integer applyCarUserType; // 用户类型
    private Integer applyVehicleClass; // 车辆类型
    private String applyRemark; // 申请描述
    private String applyFileId; // 外调车附件申请时上送的附件
    private String applyFile; // 自有车邀请上传的行驶证
    private String operCert; // 自有车邀请上传的运营证
    private Long plateNumber; // 车牌编号
    private String plateNumberStr; // 车牌号码
    private Integer state; // 状态
    private String auditRemark; // 审核描述
    private String applyPlateNumbers; // 邀请车辆
    private String beApplyTenantId; // 邀请车队id
    private Long applyTenantId; // 车队id
    private Integer applyType; // 邀请类型

    private String ApplyFileUrl; // 申请附件,小图
    private String applyCarCertPicUrl;
    private String stateName; // 状态中文
    private String applyTypeName; // 申请类型
    private Boolean canChoose; // 新邀请时可以选
    private List<ApplyRecordVehicleInfoDto> vehicles; // 车辆信息

}
