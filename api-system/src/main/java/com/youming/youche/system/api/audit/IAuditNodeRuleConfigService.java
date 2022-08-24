package com.youming.youche.system.api.audit;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.system.domain.audit.AuditNodeRuleConfig;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 节点规则配置表 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
public interface IAuditNodeRuleConfigService extends IService<AuditNodeRuleConfig> {

    List<AuditNodeRuleConfig> getAuditNodeRuleConfigList(Long nodeId,Long auditId,Long tenantId);

    Integer checkVersionRule(Long nodeId, Integer ruleVersion, Long tenantId);

    List<Map<Object, Object>> getAuditNodeRuleConfigByNodeVer(Long nodeId);

    List<Map<Object, Object>> getAuditNodeRuleConfigByNodeVerFalse(Long nodeId, Long tenantId, Integer ruleVersion);
}
