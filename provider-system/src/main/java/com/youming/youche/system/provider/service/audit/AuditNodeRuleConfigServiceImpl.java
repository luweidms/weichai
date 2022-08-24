package com.youming.youche.system.provider.service.audit;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.system.api.audit.IAuditNodeRuleConfigService;
import com.youming.youche.system.domain.audit.AuditNodeRuleConfig;
import com.youming.youche.system.provider.mapper.audit.AuditNodeRuleConfigMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 节点规则配置表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
@DubboService(version = "1.0.0")
public class AuditNodeRuleConfigServiceImpl extends ServiceImpl<AuditNodeRuleConfigMapper, AuditNodeRuleConfig>
		implements IAuditNodeRuleConfigService {

	@Override
	public List<AuditNodeRuleConfig> getAuditNodeRuleConfigList(Long nodeId, Long auditId,Long tenantId) {
		QueryWrapper<AuditNodeRuleConfig> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("audit_id",auditId)
				.eq("node_id",nodeId)
				.eq("tenant_id",tenantId);
		return baseMapper.selectList(queryWrapper);
	}

	@Override
	public Integer checkVersionRule(Long nodeId, Integer ruleVersion, Long tenantId) {
		return baseMapper.checkVersionRule(nodeId, ruleVersion, tenantId);
	}

	@Override
	public List<Map<Object, Object>> getAuditNodeRuleConfigByNodeVer(Long nodeId) {
		return baseMapper.getAuditNodeRuleConfigByNodeVer(nodeId);
	}

	@Override
	public List<Map<Object, Object>> getAuditNodeRuleConfigByNodeVerFalse(Long nodeId, Long tenantId, Integer ruleVersion) {
		return baseMapper.getAuditNodeRuleConfigByNodeVerFalse(nodeId, tenantId, ruleVersion);
	}
}
