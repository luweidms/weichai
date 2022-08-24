package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class StaffDataInfoVo implements Serializable {
    private static final long serialVersionUID = 875336885318393540L;
    private Long userId;
    /**
     * 登录名
     */
    private String loginAcct;
    /**
     * 手机号码
     */
    private String mobilePhone;
    /**
     * 姓名
     */
    private String linkman;
    /**
     * 员工工号
     */
    private String employeeNumber;
    /**
     * 职位
     */
    private String staffPosition;
    /**
     * 状态
     */
    private Integer lockFlag;

    /**
     * 员工所属组织
     */
    private Long orgId;

}
