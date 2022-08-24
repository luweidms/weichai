package com.youming.youche.record.dto;

import lombok.Data;

/**
 * Created by yangliu on 2018/4/17.
 */
@Data
public class ApplyRecordQueryDto implements java.io.Serializable {
    private static final long serialVersionUID = 6294453110434926802L;
    private String billId; // 司机账号
    private String tenantName; // 车队名称
    private String tenantLinkPhone; // 车队手机
    private Integer carUserType; // 申请类型
    private Integer applyState; // 审核状态
    private Integer type; // 申请类型
}
