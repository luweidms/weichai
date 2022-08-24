package com.youming.youche.system.api.audit;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.system.domain.audit.AuditNodeRuleConfig;
import com.youming.youche.system.domain.audit.AuditNodeRuleConfigVer;

import java.util.List;

/**
 * <p>
 * 节点规则配置表 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
public interface IAuditNodeRuleConfigVerService extends IService<AuditNodeRuleConfigVer> {

    /**
     * 保存规则配置版本表
     * @param auditNodeRuleConfigs
     * @return
     * @throws Exception
     */
    int saveAuditNodeRuleConfigVer(List<AuditNodeRuleConfig> auditNodeRuleConfigs,Long userId)throws Exception;
}
