package com.youming.youche.system.domain;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
* <p>
    * 角色权限表
    * </p>
* @author Terry
* @since 2022-01-21
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class SysPermissionBtn extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 权限 ID
            */
    private Long permissionId;

            /**
            * 按钮 ID
            */
    private Long btnId;


}
