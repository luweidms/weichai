package com.youming.youche.system.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@RequiredArgsConstructor(staticName = "of")
public class SysOrganizeDto implements Serializable {


    private static final long serialVersionUID = -7152722062573207436L;

    private Long id;
    /**
     * 组织名称
     */
    private String orgName;

    /**
     * 操作员编号
     */
    private Long opId;

    /**
     * 上级组织编号
     */
    private Long parentOrgId;
    private String  parentOrgName;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 状态;默认0(不可用),1(可用)
     */
    private Integer state;

    /**
     * 部门负责人ID
     */
    private Long userInfoId;

    /**
     * 部门负责人姓名
     */
    private String linkMan;
}
