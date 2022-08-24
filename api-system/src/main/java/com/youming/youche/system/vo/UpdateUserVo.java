package com.youming.youche.system.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UpdateUserVo implements Serializable {


    private static final long serialVersionUID = 3514612385801302157L;
    /**
     * 员工姓名
     */
    @NotBlank
    private String linkman;
    /**
     * 员工身份证
     */
    @NotBlank
    private String identification;

    @NotNull(message = "用户id必填")
    private Long userInfoId;
    /**
     * 账号状态 1启用 2关闭
     */
    @Min(0)
    private Integer lockFlag;
    /**
     * 员工职位
     */
    private String staffPosition;
    /**
     * 员工工号
     */
    private String employeeNumber;
    /**
     * 部门id 多个已逗号隔开
     */
    @NotBlank(message = "请选择部门")
    private String orgIds;
}
