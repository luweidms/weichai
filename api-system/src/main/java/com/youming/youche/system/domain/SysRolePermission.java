package com.youming.youche.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 角色权限表
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(excludeProperty = { "createTime", "updateTime" })
public class SysRolePermission extends BaseDomain {

	private static final long serialVersionUID = 1L;

	/**
	 * 角色 ID
	 */
	private Long roleId;

	/**
	 * 权限 ID
	 */
	private Long permissionId;

}
