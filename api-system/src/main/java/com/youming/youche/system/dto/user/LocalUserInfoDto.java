package com.youming.youche.system.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hzx
 * @date 2022/4/21 13:27
 */
@Data
public class LocalUserInfoDto implements Serializable {

    private static final long serialVersionUID = -334583010554498413L;

    /**
     * 联系人姓名
     */
    private String linkman;

    /**
     * 身份证号码
     */
    private String identification;

    /**
     * 联系人手机
     */
    private String loginAcct;

    /**
     * 员工工号
     */
    private String employeeNumber;

    /**
     * 职位
     */
    private String staffPosition;

    /**
     * 租户名称(车队全称)
     */
    private String tenantName;

    /**
     * 联系人手机
     */
    private String adminUserMobile;

    private String receiverName;

}
