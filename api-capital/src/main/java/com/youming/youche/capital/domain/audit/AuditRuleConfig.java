package com.youming.youche.capital.domain.audit;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 规则配置
 * </p>
 *
 * @author Terry
 * @since 2022-01-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AuditRuleConfig extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 审核配置表主键
     */
    private Long auditId;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则文字说明
     */
    private String ruleTips;

    /**
     * 规则类型
     */
    private Integer ruleType;

    /**
     * 规则实现类
     */
    private String targetObj;

    /**
     * 创建人
     */
    private Long opId;

    /**
     * 对应传入的map的key值
     */
    private String ruleKey;

    /**
     * 规则编码
     */
    private String ruleCode;

    /**
     * 版本号
     */
    private Integer version;


}
