package com.youming.youche.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 系统角色表;一个角色可以关联多个操作员，一个操作员可以有多个角色。;一个角色可以关联多个权限，一个权限可以被多个角色拥有。
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysRole extends BaseDomain {

	private static final long serialVersionUID = 1L;

	/**
	 * 角色名称
	 */
	private String roleName;

	/**
	 * 状态;默认0(不可用),1(可用)
	 */
	private Integer state;

	/**
	 * 操作人;对应sys_user中的id
	 */
	private Long lastOpUserId;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 租户id
	 */
	private Long tenantId;

	/**
	 * 操作人,对应sys_user的id
	 */
	private Long opUserId;

	/**
	 * 租户角色类型（1：位租户的超级管理员，不允许删除，2：租户自定义角色）
	 */
	private Integer roleType;

}
