package com.youming.youche.system.api.audit;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.system.domain.audit.AuditRuleConfig;

import java.util.List;

/**
 * <p>
 * 规则配置 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
public interface IAuditRuleConfigService extends IService<AuditRuleConfig> {

    public List<AuditRuleConfig> getAuditRuleConfigByIdList(List<Long> ruleIds);
    public List<AuditRuleConfig> getAuditRuleConfigList(long auditId);
}
