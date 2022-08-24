package com.youming.youche.record.dto.driver;

import lombok.Data;

import java.io.Serializable;

/**
 * @version:
 * @Title: DoQueryDriversDto
 * @Package: com.youming.youche.dto.driver
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/1/18 13:50
 * @company:
 */
@Data
public class DoQueryDriversDto implements Serializable {


    private static final long serialVersionUID = -5405703014782245418L;
    private String loginAcct; // 用户账号

    private String linkman; // 用户名称

    private Integer carUserType; // 用户类型

    private String carUserTypeName; // 用户类型名称

    private Long attachTenantId; // 车队id

    private String attachTenantName; // 车队名称

    private String attachTennantLinkman; // 车队联系人

    private String attachTennantLinkPhone; // 车队联系人手机号

    private Integer vehicleNum; // 车辆数

    private Integer state; // 司机状态

    private String stateName;

    private String hasVer;

    private Long userId; // 用户编号

    private String createDate; // 创建时间

    private Long relId; // 审核主键

    /**
     * 审核原因
     */
    private String authReason;

    private String authUserId; // 审核用户Id

    private String authUserName; // 审核用户

    private String authOrgId; // 审核组织id

    private String authOrgIdName; // 审核组织名称

    private String driverLicenseExpiredDay; // 驾照到期日

    private String qcCertiExpiredDay; // 运营证国企日

    private String driverLicenseExpiredTime; // 驾照过期时间

    private String qcCertiExpriedTime; // 证书过期时间

    private String expiredString; // 到期证件

    private Integer bindState; // 是否绑定卡

    private String bindStateName;

    private String identification; // 省份证
}
