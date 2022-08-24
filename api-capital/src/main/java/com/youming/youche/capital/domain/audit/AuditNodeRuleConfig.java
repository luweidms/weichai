package com.youming.youche.capital.domain.audit;

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
 * @since 2022-01-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AuditNodeRuleConfig extends BaseDomain {

    private static final long serialVersionUID = 1L;


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


}
