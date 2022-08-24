package com.youming.youche.system.provider.service.audit;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.system.api.audit.IAuditNodeRuleConfigVerService;
import com.youming.youche.system.domain.audit.AuditNodeRuleConfig;
import com.youming.youche.system.domain.audit.AuditNodeRuleConfigVer;
import com.youming.youche.system.provider.mapper.audit.AuditNodeRuleConfigVerMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class AuditNodeRuleConfigVerServiceImpl extends ServiceImpl<AuditNodeRuleConfigVerMapper, AuditNodeRuleConfigVer>
		implements IAuditNodeRuleConfigVerService {

	@Override
	public int saveAuditNodeRuleConfigVer(List<AuditNodeRuleConfig> auditNodeRuleConfigs,Long userId) throws Exception {
		long tenantId = -1;
		Integer version = null;
		long auditId = -1;
		List<Long> ids = new ArrayList<>();
		for (AuditNodeRuleConfig auditNodeRuleConfig : auditNodeRuleConfigs) {
			ids.add(auditNodeRuleConfig.getId());
			version = auditNodeRuleConfig.getVersion();
			auditId = auditNodeRuleConfig.getAuditId();
			tenantId = auditNodeRuleConfig.getTenantId();
		}
		version = version == null ? 1 : version+1;
		if(ids.size() > 0){
			Integer i = checkRuleVer(ids, tenantId, version, auditId);
			for (AuditNodeRuleConfig auditNodeRuleConfig : auditNodeRuleConfigs) {
				if(i==null||i <= 0){
					AuditNodeRuleConfigVer auditNodeRuleConfigVer = new AuditNodeRuleConfigVer();
					BeanUtil.copyProperties(auditNodeRuleConfig,auditNodeRuleConfigVer);
					auditNodeRuleConfigVer.setVerId(auditNodeRuleConfig.getId());
					auditNodeRuleConfigVer.setUpdateTime(LocalDateTime.now());
					auditNodeRuleConfigVer.setUpdateOpId(userId);
					save(auditNodeRuleConfigVer);
				}
			}
		}
		return version;
	}

	/**
	 * 是否已经存在该版本节点信息了
	 * @param ids
	 * @param tenantId
	 * @param version
	 * @param auditId
	 * @author qiulf
	 * @return
	 */
	public int checkRuleVer(List<Long> ids,long tenantId,Integer version,long auditId){
		QueryWrapper<AuditNodeRuleConfigVer> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("tenant_id",tenantId)
				.eq("id",ids)
				.eq("audit_id",auditId);
		if(version == null){
			queryWrapper.isNull("version");
		}else {
			queryWrapper.eq("version",version);
		}
		queryWrapper.select("COUNT(*) as number");
		Map<String , Object> map = this.getMap(queryWrapper);
		if(map!=null){
			return Integer.valueOf(String.valueOf(map.get("number")));
		}else {
			return 0;
		}
	}
}
