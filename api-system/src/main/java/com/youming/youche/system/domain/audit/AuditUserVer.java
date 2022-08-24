package com.youming.youche.system.domain.audit;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 节点审核人版本表
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AuditUserVer extends BaseDomain {



	/**
	 * 原表的主键
	 */
	private Long verId;

	/**
	 * 版本号
	 */
	private Long version;

	/**
	 * 节点的主键
	 */
	private Long nodeId;

	/**
	 * 操作人
	 */
	private Long opId;

	/**
	 * 租户
	 */

	private Long tenantId;

	/**
	 * 目标的用户，角色，部门的id
	 */
	private Long targetObjId;

	/**
	 * 类型
	 */
	private Integer targetObjType;

}