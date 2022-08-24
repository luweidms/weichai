package com.youming.youche.system.provider.service.audit;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.system.api.audit.IAuditRuleConfigService;
import com.youming.youche.system.domain.audit.AuditRuleConfig;
import com.youming.youche.system.provider.mapper.audit.AuditRuleConfigMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * <p>
 * 规则配置 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
@DubboService(version = "1.0.0")
public class AuditRuleConfigServiceImpl extends ServiceImpl<AuditRuleConfigMapper, AuditRuleConfig>
		implements IAuditRuleConfigService {

	@Override
	public List<AuditRuleConfig> getAuditRuleConfigByIdList(List<Long> ruleIds) {
		QueryWrapper<AuditRuleConfig> queryWrapper=new QueryWrapper<>();
		queryWrapper.in("id",ruleIds);
		return baseMapper.selectList(queryWrapper);
	}

	@Override
	public List<AuditRuleConfig> getAuditRuleConfigList(long auditId) {
		QueryWrapper<AuditRuleConfig> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("audit_id",auditId);
		return baseMapper.selectList(queryWrapper);
	}
}
