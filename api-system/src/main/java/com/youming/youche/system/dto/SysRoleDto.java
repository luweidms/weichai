package com.youming.youche.system.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@RequiredArgsConstructor(staticName = "of")
@Accessors(chain = true)
public class SysRoleDto implements Serializable {

    private static final long serialVersionUID = -4624689036478635140L;

    private Long roleId;
    private String roleName;
    private String createTime;
    private String opName;
    private Integer count;

    /**
     * 租户角色类型（1：位租户的超级管理员，不允许删除，2：租户自定义角色）
     */
    private Integer roleType;

}
