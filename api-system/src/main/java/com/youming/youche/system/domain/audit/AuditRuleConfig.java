package com.youming.youche.system.domain.audit;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import java.time.LocalDateTime;

/**
 * <p>
 * 规则配置
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AuditRuleConfig extends BaseDomain {



//	/**
//	 * 主键
//	 */
//	@Id // 主键
//	@TableId(value = "RULE_ID", type = IdType.AUTO)
//	@Column(name = "RULE_ID")
//	private Long ruleId;

	/**
	 * 审核配置表主键
	 */
	@TableField("AUDIT_ID")
	@Column(name = "AUDIT_ID")
	private Long auditId;

	/**
	 * 规则名称
	 */
	@TableField("RULE_NAME")
	@Column(name = "RULE_NAME")
	private String ruleName;

	/**
	 * 规则文字说明
	 */
	@TableField("RULE_TIPS")
	@Column(name = "RULE_TIPS")
	private String ruleTips;

	/**
	 * 规则类型
	 */
	@TableField("RULE_TYPE")
	@Column(name = "RULE_TYPE")
	private Integer ruleType;

	/**
	 * 规则实现类
	 */
	@TableField("TARGET_OBJ")
	@Column(name = "TARGET_OBJ")
	private String targetObj;

	/**
	 * 创建时间
	 */
	@TableField("CREATE_DATE")
	@Column(name = "CREATE_DATE")
	private LocalDateTime createDate;

	/**
	 * 创建人
	 */
	@TableField("OP_ID")
	@Column(name = "OP_ID")
	private Long opId;

	/**
	 * 对应传入的map的key值
	 */
	@TableField("RULE_KEY")
	@Column(name = "RULE_KEY")
	private String ruleKey;

	/**
	 * 规则编码
	 */
	@TableField("RULE_CODE")
	@Column(name = "RULE_CODE")
	private String ruleCode;

	/**
	 * 版本号
	 */
	@TableField("VERSION")
	@Column(name = "VERSION")
	private Integer version;

}
