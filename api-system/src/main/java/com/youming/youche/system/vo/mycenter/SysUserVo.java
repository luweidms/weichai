package com.youming.youche.system.vo.mycenter;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName SysUserQueryVo
 * @Description 添加描述
 * @Author zag
 * @Date 2022/2/18 10:12
 */
@Data
public class SysUserVo implements Serializable {

    /** 员工帐号 */
    private String loginAcct;
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
