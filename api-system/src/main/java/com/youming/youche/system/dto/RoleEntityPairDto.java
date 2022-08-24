package com.youming.youche.system.dto;

import com.youming.youche.system.domain.SysRole;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class RoleEntityPairDto implements Serializable {

    /**
     * 系统角色
     */
    SysRole sysRole;

    /**
     * 实体集合
     */
    List<Long> entityIdList;

    public RoleEntityPairDto(SysRole role, List<Long> entityIdList) {
        this.sysRole = role;
        this.entityIdList = entityIdList;
    }

}
