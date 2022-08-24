package com.youming.youche.record.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class QueryTenantDto implements Serializable {

    private static final long serialVersionUID = 7240881835840081500L;

    private Long relId; // 车帘租户关系表主键
    private Integer vehicleClass; // 车辆类型
    private String createDate; // 创建日期
    private String billReceiverMobile; // 账单接受人手机号
    private String billReceiverName; // 账单接受人名称
    private Integer tenantId; // 车队id
    private String tenantName; // 车队名称
    private String linkMan; // 联系人
    private String linkPhone; // 联系手机号
    private String vehicleClassName;  // 车辆类型 名称
    private Long orderCount; // 订单数

}
