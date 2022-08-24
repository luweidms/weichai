package com.youming.youche.system.provider.service.audit;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.system.api.audit.IAuditNodeConfigService;
import com.youming.youche.system.api.audit.IAuditNodeConfigVerService;
import com.youming.youche.system.domain.audit.AuditNodeConfig;
import com.youming.youche.system.domain.audit.AuditNodeConfigVer;
import com.youming.youche.system.provider.mapper.audit.AuditNodeConfigMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 节点配置表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
@DubboService(version = "1.0.0")
public class AuditNodeConfigServiceImpl extends ServiceImpl<AuditNodeConfigMapper, AuditNodeConfig>
		implements IAuditNodeConfigService {

	@Resource
	private  IAuditNodeConfigVerService iAuditNodeConfigVerService;


	@Override
	public int saveAuditNodeConfigVer(Long auditId,Long tenantId,Long userId) throws Exception {
		List<AuditNodeConfig> auditNodeConfigs = getAuditNodeConfigList(auditId,tenantId);
		List<Long> nodeIds = new ArrayList<>();
		Integer version = null;
		long tenantIds = -1;
		for (AuditNodeConfig auditNodeConfig : auditNodeConfigs) {
			nodeIds.add(auditNodeConfig.getId());
			version = auditNodeConfig.getVersion();
			tenantIds = auditNodeConfig.getTenantId();
			auditId = auditNodeConfig.getAuditId();
		}
		version = version == null ? 1 : version+1;
		if(nodeIds.size() > 0){
			Integer i = iAuditNodeConfigVerService.checkNodeVer(nodeIds, tenantIds, version, auditId);
			for (AuditNodeConfig auditNodeConfig : auditNodeConfigs) {
				if(i == null ||  i <= 0){
					AuditNodeConfigVer auditNodeConfigVer = new AuditNodeConfigVer();
					BeanUtil.copyProperties(auditNodeConfig,auditNodeConfigVer);
					auditNodeConfigVer.setNodeId(auditNodeConfig.getId());
					auditNodeConfigVer.setUpdateTime(LocalDateTime.now());
					auditNodeConfigVer.setUpdateOpId(userId);
					iAuditNodeConfigVerService.saveOrUpdate(auditNodeConfigVer);
				}
				auditNodeConfig.setVersion(version);
				saveOrUpdate(auditNodeConfig);
			}
		}
		return version;
	}

	@Override
	public List<AuditNodeConfig> getAuditNodeConfigList(long auditId,long tenantId){
		LambdaQueryWrapper<AuditNodeConfig> LambdaQueryWrapper = Wrappers.lambdaQuery();
		LambdaQueryWrapper.eq(AuditNodeConfig::getTenantId, tenantId)
				.eq(AuditNodeConfig::getAuditId,auditId);
		return baseMapper.selectList(LambdaQueryWrapper);
	}


	@Override
	public List<AuditNodeConfig> getAuditNodeConfigByParentId(Long auditId, Long parentNodeId,Long tenantId) {
		QueryWrapper<AuditNodeConfig> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("audit_id",auditId)
				.eq("tenant_id",tenantId)
				.eq("parent_node_id",parentNodeId).orderByDesc("create_time");
		return baseMapper.selectList(queryWrapper);
	}

	@Override
	public AuditNodeConfig getAuditNodeConfig(long nodeId) {
		return baseMapper.selectById(nodeId);
	}


	@Override
	public AuditNodeConfig getAuditNodeByParentId(long parentNodeId) {
		QueryWrapper<AuditNodeConfig> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("parent_node_id",parentNodeId);
		List<AuditNodeConfig> list =baseMapper.selectList(queryWrapper);
		if(list != null && list.size() > 0){
			return list.get(0);
		}
		return null;
	}

	@Override
	public boolean checkVersionNode(Integer version, long nodeId) {
		QueryWrapper<AuditNodeConfig> queryWrapper=new QueryWrapper<>();
		if(version == null){
			queryWrapper.isNull("version");
		}else {
			queryWrapper.eq("version",version);
		}
		queryWrapper.eq("id",nodeId);
		Integer i=baseMapper.selectCount(queryWrapper);
		if(i > 0){
			return true;
		}
		return false;
	}

	@Override
	public AuditNodeConfig getNextAuditNodeConfig(Long nodeId) {
		LambdaQueryWrapper<AuditNodeConfig> auditNodeConfigLambdaQueryWrapper = Wrappers.lambdaQuery();
		auditNodeConfigLambdaQueryWrapper.eq(AuditNodeConfig::getParentNodeId,nodeId);
		List<AuditNodeConfig> auditConfigs = super.list(auditNodeConfigLambdaQueryWrapper);
		if (auditConfigs == null || auditConfigs.size() == 0) {
			return null;
		} else {
			if (auditConfigs.size() == 1) {
				return auditConfigs.get(0);
			} else {
				throw new BusinessException("配置的审核节点的下一个节点重复，当前的节点的主键[" + nodeId + "]");
			}
		}
	}
	@Override
	public AuditNodeConfig getNextAuditNodeConfig(Long nodeId,List<AuditNodeConfig> auditConfigs) {
		if (auditConfigs == null || auditConfigs.size() == 0) {
			return null;
		} else {
			if (auditConfigs.size() == 1) {
				return auditConfigs.get(0);
			} else {
				throw new BusinessException("配置的审核节点的下一个节点重复，当前的节点的主键[" + nodeId + "]");
			}
		}
	}

	@Override
	public Integer countId(Integer version, Long nodeId) {
		LambdaQueryWrapper<AuditNodeConfig> queryWrapper=Wrappers.lambdaQuery();
		if(version == null){
			queryWrapper.isNull(AuditNodeConfig::getVersion);
		}else {
			queryWrapper.eq(AuditNodeConfig::getVersion,version);
		}
		if (null!=nodeId){
			queryWrapper.eq(AuditNodeConfig::getParentNodeId,nodeId);
		}
		return count(queryWrapper);
	}

	@Override
	public List<AuditNodeConfig> getNextAuditNodeConfigList(Long nodeId) {
		LambdaQueryWrapper<AuditNodeConfig> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(AuditNodeConfig::getParentNodeId,nodeId);
		return list(wrapper);
	}

	@Override
	public List<AuditNodeConfig> getAll() {
		return list();

	}
}
