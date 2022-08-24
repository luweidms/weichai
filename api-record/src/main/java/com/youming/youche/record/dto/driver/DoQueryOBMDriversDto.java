package com.youming.youche.record.dto.driver;

import lombok.Data;

import java.io.Serializable;

/**
 * @version:
 * @Title: DoQueryOBMDriversDto
 * @Package: com.youming.youche.record.dto.driver
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/2/16 18:36
 * @company:
 */
@Data
public class DoQueryOBMDriversDto implements Serializable {

    private String mobilePhone; // 手机号

    private String linkman; // 姓名

    private String identification; // 省份证

    private Long tenantId; // 车队id

    private String attachTenantName; //车队名称

    private String attachTenantLinkman; // 车队全程

    private String attachTenantLinkPhone; // 车队手机号

    private Integer vehicleNum; // 车辆数

    private Long userId; // 用户编号i

    private Integer state; // 认证状态

    private String stateName;

    private String hasVer; // 审核

    private String authReason; // 审核意见

    private Long authUserId; // 审核用户id

    private String authUserName; // 审核人名称

    private Long relId; // 审核业务主键

    private String createDate; // 创建时间

    private Integer bindState; // 是否绑定卡

    private String bindStateName;
}
