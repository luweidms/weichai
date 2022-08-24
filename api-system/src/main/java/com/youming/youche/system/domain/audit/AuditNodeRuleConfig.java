package com.youming.youche.system.domain.audit;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
* <p>
    * 节点规则配置表
    * </p>
* @author CaoYaJie
* @since 2022-02-15
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AuditNodeRuleConfig extends BaseDomain {



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
