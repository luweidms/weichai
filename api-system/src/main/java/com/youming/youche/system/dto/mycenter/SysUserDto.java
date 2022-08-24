package com.youming.youche.system.dto.mycenter;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName SysUserDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/2/18 16:25
 */
@Data
public class SysUserDto implements Serializable {

    /** 用户id */
    private Long userId;
    /** 员工帐号 */
    private String loginAcct;
    /** 联系人姓名 */
    private String linkman;
    /** 联系人手机 */
    private String mobilePhone;
    /** 身份证号 */
    private String identification;
    /** 员工姓名 */
    private String staffName;
    /** 员工工号 */
    private String employeeNumber;
    /** 员工职位 */
    private String staffPosition;
    /** 员工部门Ids */
    private String orgIds;
    /** 员工部门Names */
    private String orgNames;
}
