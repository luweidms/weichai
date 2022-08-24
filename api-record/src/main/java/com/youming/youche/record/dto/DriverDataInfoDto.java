package com.youming.youche.record.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DriverDataInfoDto implements Serializable {
    /**
     * 联系人手机
     */
    private String mobilePhone;

    /**
     * 服务商联系人
     */
    private String linkman;

    /**
     * 会员类型
     */
    private Integer carUserType;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 名称租户
     */
    private String name;
    /**
     * 租户联系人
     */
    private String linkMan;

    /**
     * 联系电话
     */
    private String linkPhone;

    /**
     * 车辆总数
     */
    private Integer vehicleNum;

    /**
     * 状态 0 禁用 1 启用
     */
    private Integer state;

    /**
     * 审核
     */
    private Integer hasVer;

    private Long id;
    /**
     * 创建时间
     */
    private String createDate;

    private Long relId;
    /**
     * 启用禁用原因
     */
    private String stateReason;

    /**
     * 审核人
     */
    private Long authManId;

    private Long authOrgId;

    /**
     * 身份证号码
     */
    private String identification;

    /**
     * 驾驶证有效期
     */
    private String driverLicenseExpiredTime;
    /**
     * 从业资格证有效期
     */
    private String qcCertiExpiredTime;
}
