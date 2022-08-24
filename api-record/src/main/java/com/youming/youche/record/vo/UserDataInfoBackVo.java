package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Date:2022/1/13
 */
@Data
public class UserDataInfoBackVo implements Serializable {

    private Long userId;

    private String linkman;

    private String mobilePhone;

    private Integer carUserType;

    private String carUserTypeName;

    private Long attachIsAdminUser;

    private Long attachTenantId;

    private String code;

    private String msg;

    private Long tenantId;

    private Integer state; // 是否已认证
}
