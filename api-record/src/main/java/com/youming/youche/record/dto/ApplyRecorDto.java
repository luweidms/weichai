package com.youming.youche.record.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApplyRecorDto implements Serializable {
    private Long vehicleCode; // 车辆编号
    private Integer applyVehicleClass; // 车辆类型
    private String applyRemark; // 申请说明
    private Long applyRecordId;  // 邀请记录主键ID ， 再次邀请或者申请平台仲裁才有
    private Integer applyState; // 申请类型    0普通申请  3平台仲裁申请
    private String auditRemark;   // 申请说明
    private Long applyFileId; // 附件ID
    private Long applyDriverUserId; // 司机id
    private String drivingLicense; // 行驶证图片ID
    private String operCerti; // 运营证图片ID
    private String billReceiverMobile; // 账单接收人手机
    private String billReceiverName; // 账单接收人名称
    private Long billReceiverUserId; // 账单就收人用户编号
    private Long belongDriverUserId; // 司机编号
    private Long publicVehicle; // 车辆图片
}
