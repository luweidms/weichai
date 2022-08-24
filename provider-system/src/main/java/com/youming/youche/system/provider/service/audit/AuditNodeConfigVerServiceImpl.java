package com.youming.youche.system.provider.service.audit;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.system.api.audit.IAuditNodeConfigVerService;
import com.youming.youche.system.domain.audit.AuditNodeConfigVer;
import com.youming.youche.system.provider.mapper.audit.AuditNodeConfigVerMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 节点配置版本表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
@DubboService(version = "1.0.0")
public class AuditNodeConfigVerServiceImpl extends ServiceImpl<AuditNodeConfigVerMapper, AuditNodeConfigVer>
		implements IAuditNodeConfigVerService {

	@Override
	public int checkNodeVer(List<Long> nodeIds, long tenantId, Integer version, long auditId) {
		QueryWrapper<AuditNodeConfigVer> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("tenant_Id",tenantId)
				.in("id",nodeIds)
				.eq("audit_id",auditId);
		if(version==null){
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

	@Override
	public AuditNodeConfigVer getAuditNodeConfigVer(Integer version, long nodeId, boolean isParent, Long tenantId) {
		QueryWrapper<AuditNodeConfigVer> queryWrapper=new QueryWrapper<>();
		if(isParent){
			queryWrapper.eq("parent_node_id",nodeId);
		}else{
			queryWrapper.eq("node_id",nodeId);
		}
		queryWrapper.eq("tenant_Id",tenantId);
		if(version==null){
			queryWrapper.isNull("version");
		}else {
			queryWrapper.eq("version",version);
		}
		List<AuditNodeConfigVer> list = baseMapper.selectList(queryWrapper);
		if(list != null && list.size() > 0){
			return list.get(0);
		}
		return null;
	}

	@Override
	public AuditNodeConfigVer getAuditNodeConfigVerIsTrue(Integer version, Long nodeId, Long tenantId) {
		return baseMapper.getAuditNodeConfigVerIsTrue(version, nodeId, tenantId);
	}

	@Override
	public AuditNodeConfigVer getAuditNodeConfigVerIsFalse(Integer version, Long nodeId, Long tenantId) {
		return baseMapper.getAuditNodeConfigVerIsFalse(version, nodeId, tenantId);
	}
}
