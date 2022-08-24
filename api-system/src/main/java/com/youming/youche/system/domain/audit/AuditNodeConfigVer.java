package com.youming.youche.system.domain.audit;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 节点配置版本表
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AuditNodeConfigVer extends BaseDomain {


//	/**
//	 * 节点主键
//	 */
//	@Id // 主键
//	@TableField("NODE_ID")
//	@Column(name = "NODE_ID")
//	private Long nodeId;

	/**
	 * 审核表的主键
	 */
	private Long auditId;

	/**
	 * 上个节点的id
	 */
	private Long parentNodeId;

	/**
	 * 节点名称
	 */
	private String nodeName;

	/**
	 * 版本
	 */
	private Integer version;

	/**
	 * 操作人
	 */
	private Long opId;

	/**
	 * 租户
	 */
	private Long tenantId;

	/**
	 * 审核节点主键
	 */
	private Long nodeId;


	/**
	 * 修改人
	 */
	private Long updateOpId;

}
