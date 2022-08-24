package com.youming.youche.market.dto.facilitator;

import lombok.Data;

import java.io.Serializable;
@Data
public class ServiceProductInfoDto implements Serializable {

    //创建开始时间
    private String beginTime;
    //创建结束时间
    private String endTime;
    //状态
    private Integer state;
    //是否共享
    private Integer isShare;
    //服务商类型
    private Integer serviceType;
    //创建车队
    private String tenantName;
    //车队联系人
    private String tenantLinkMan;
    //车队电话
    private String tenantCall;
    //站点名称
    private String productName;
    //
    private String loginAcct;
    //服务商名称
    private String serviceName;
    //联系人
    private String linkMan;

    private Integer isAudit;

    private Integer authState;

    private Long serviceUserId;

    private String serviceCall;

    private String address;

    private String billBeginPrice;
    private String billEndPrice;
}
