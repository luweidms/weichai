package com.youming.youche.system.domain.audit;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Id;

/**
 * <p>
 * 节点配置表
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AuditNodeConfig  extends BaseDomain {


//	/**
//	 * 主键
//	 */
//	@Id // 主键
//	@TableId(value = "NODE_ID", type = IdType.AUTO)
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
	 * 是否退回发起人0不退回1退回发起人
	 */
	private Integer flag;

}
