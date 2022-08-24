package com.youming.youche.system.domain;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 系统角色操作员关系表;ROLE_ID : 对应sys_role中的role_id;op_id : 对应sys_operator中的operator_id
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysUserRole extends BaseDomain {

	private static final long serialVersionUID = 1L;

	/**
	 * 角色ID;对应sy_role中的id
	 */
	private Long roleId;

	/**
	 * 操作员编号,对应sy_user的id
	 */
	private Long userId;

	/**
	 * 状态;默认0(不可用),1(可用)
	 */
	private Integer state;

	/**
	 * 操作人,对应sys_user的id
	 */
	private Long lastOpUserId;

	private String remark;

	private Long tenantId;

}
