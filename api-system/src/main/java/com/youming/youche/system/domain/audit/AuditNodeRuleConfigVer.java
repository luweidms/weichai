package com.youming.youche.system.domain.audit;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 节点规则配置表
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AuditNodeRuleConfigVer extends BaseDomain {


//	/**
//	 * ID
//	 */
//	@TableField(value = "ID")
//	@Column(name = "ID")
//	private Long id;

	/**
	 * 规则主键
	 */
	private Long ruleId;

	/**
	 * 审核配置表主键
	 */
	private Long auditId;

	/**
	 * 节点主键
	 */
	private Long nodeId;

	/**
	 * 规则值
	 */
	private String ruleValue;


	/**
	 * 创建人
	 */
	private Long opId;

	/**
	 * 租户
	 */
	private Long tenantId;

	/**
	 * 版本号
	 */
	private Integer version;

	/**
	 * 版本ID
	 */
	private Long verId;


	/**
	 * 修改人
	 */
	private Long updateOpId;

}
