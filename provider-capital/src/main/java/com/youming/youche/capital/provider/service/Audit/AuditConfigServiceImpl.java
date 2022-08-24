package com.youming.youche.capital.provider.service.Audit;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.capital.api.iaudit.IAuditConfigService;
import com.youming.youche.capital.domain.audit.*;
import com.youming.youche.capital.provider.mapper.audit.AuditConfigMapper;
import com.youming.youche.capital.provider.mapper.audit.AuditNodeConfigMapper;
import com.youming.youche.capital.provider.mapper.audit.AuditNodeInstMapper;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.AuditConsts;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 审核配置主表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
@DubboService(version = "1.0.0")
public class AuditConfigServiceImpl extends ServiceImpl<AuditConfigMapper, AuditConfig> implements IAuditConfigService {

    @Autowired
    AuditConfigMapper auditConfigMapper;

    @Autowired
    AuditNodeConfigMapper auditNodeConfigMapper;

    @Autowired
    AuditNodeInstMapper auditNodeInstMapper;

    @Override
    public AuditConfig getAuditConfigByCode(String auditCode) {
        QueryWrapper<AuditConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("AUDIT_CODE", auditCode);
        List<AuditConfig> auditConfigs = auditConfigMapper.selectList(queryWrapper);
        if (auditConfigs == null || auditConfigs.size() == 0) {
            return null;
        } else {
            if (auditConfigs.size() == 1) {
                return auditConfigs.get(0);
            } else {
                throw new BusinessException("配置的审核业务编码重复，审核配置编码[" + auditCode + "]");
            }
        }
    }

    @Override
    public AuditNodeConfig getFirstAuditNodeConfig(Long auditId, Long tenantId) {
        QueryWrapper<AuditNodeConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("AUDIT_ID", auditId).eq("PARENT_NODE_ID", -1L);
        if (tenantId != null) {
            queryWrapper.eq("TENANT_ID", tenantId);
        }

        List<AuditNodeConfig> auditConfigs = auditNodeConfigMapper.selectList(queryWrapper);
        if (auditConfigs == null || auditConfigs.size() == 0) {
            return null;
        } else {
            if (auditConfigs.size() == 1) {
                return auditConfigs.get(0);
            } else {
                throw new BusinessException("配置的审核节点的第一个节点重复，审核配置主键[" + auditId + "]");
            }
        }
    }


    @Override
    public AuditNodeInst queryAuditIng(String busiCode, Long busiId, Long tenantId) {
        QueryWrapper<AuditNodeInst> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("BUSI_ID", busiId).eq("AUDIT_CODE", busiCode).
                eq("STATUS", AuditConsts.Status.AUDITING).eq("TENANT_ID", tenantId)
                .eq("AUDIT_RESULT", AuditConsts.RESULT.TO_AUDIT);
        List<AuditNodeInst> auditNodeInsts = auditNodeInstMapper.selectList(queryWrapper);
        if (auditNodeInsts != null && auditNodeInsts.size() > 0) {
            if (auditNodeInsts.size() == 1) {
                return auditNodeInsts.get(0);
            } else {
                throw new BusinessException("业务编码[" + busiCode + "],业务主键[" + busiId + "] 数据有多条在审核的流程");
            }
        }
        return null;
    }

    @Override
    public boolean isAuditIng(String busiCode, Long busiId, Long tenantId) {
        QueryWrapper<AuditNodeInst> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("BUSI_ID", busiId).eq("AUDIT_CODE", busiCode).
                eq("STATUS", AuditConsts.Status.AUDITING).eq("TENANT_ID", tenantId)
                .isNull("AUDIT_DATE");
        List<AuditNodeInst> auditNodeInsts = auditNodeInstMapper.selectList(queryWrapper);
        if (auditNodeInsts != null && auditNodeInsts.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public Integer getAuditRuleVersionNo(Long nodeId) throws BusinessException {
        List<Integer> list = auditNodeInstMapper.getAuditRuleVersionNo(nodeId);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public boolean checkNodePreRule(Long nodeId, Long busiId, Map params, Integer ruleVersion, Long tenantId) throws BusinessException {
        boolean isNew = checkVersionRule(ruleVersion, nodeId, tenantId);
        Map map = getAuditNodeRuleConfigByNodeVer(nodeId, ruleVersion, isNew, tenantId);
        //节点有配置规则，如果有多个规则，有一个满足就可以到下一个节点
        if (map != null) {
            boolean pass = false;
            List<AuditRuleConfig> ruleList = (List<AuditRuleConfig>) map.get("auditNodeConfigs");
            for (int i = 0; i < ruleList.size(); i++) {
                AuditRuleConfig auditRuleConfig = ruleList.get(i);
                String ruleValue = "";
                if (isNew) {
                    List<AuditNodeRuleConfig> list = (List<AuditNodeRuleConfig>) map.get("auditNodeRuleConfigs");
                    if (list != null && list.size() > 0) {
                        AuditNodeRuleConfig auditNodeRuleConfig = list.get(i);
                        ruleValue = auditNodeRuleConfig.getRuleValue();
                    }
                } else {
                    List<AuditNodeRuleConfigVer> list = (List<AuditNodeRuleConfigVer>) map.get("auditNodeRuleConfigVers");
                    if (list != null && list.size() > 0) {
                        AuditNodeRuleConfigVer auditNodeRuleConfigVer = list.get(i);
                        ruleValue = auditNodeRuleConfigVer.getRuleValue();
                    }
                }
                if (checkRule(auditRuleConfig.getRuleType(), ruleValue, auditRuleConfig.getTargetObj(), auditRuleConfig.getRuleKey(), busiId, params)) {
                    pass = true;
                    break;
                }
            }
            //前置规则都没有满足，不需要走流程
            return pass;
        }
        return true;
    }

    /**
     * 校验是否最新版本的规则
     *
     * @param version
     * @param nodeId
     * @return
     */
    public boolean checkVersionRule(Integer version, long nodeId, Long tenantId) {
        Integer i = auditNodeInstMapper.checkVersionRule(version, nodeId, tenantId);
        if (i > 0) {
            return true;
        }
        return false;
    }

    public boolean checkRule(Integer ruleType, String ruleValue,
                             String targetObj, String ruleKey, Long busiId, Map<String, Object> params) throws BusinessException {
        try {
            Class cls = Class.forName(targetObj);
            Method m = cls.getDeclaredMethod("execute", new Class[]{Integer.class, String.class, Long.class, Map.class, String.class});
            Boolean result = (Boolean) m.invoke(cls.newInstance(), ruleType, ruleValue, busiId, params, ruleKey);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * @param nodeId
     * @param version
     * @param isNew
     * @return
     * @throws Exception
     */
    private Map getAuditNodeRuleConfigByNodeVer(Long nodeId, Integer version, boolean isNew, Long tenantId) throws BusinessException {
        Map map = null;
        List<AuditNodeConfig> auditNodeConfigs = null;
        List<AuditNodeRuleConfig> auditNodeRuleConfigs = null;
        List<AuditNodeRuleConfigVer> auditNodeRuleConfigVers = null;
        if (isNew) {
            auditNodeConfigs = auditNodeInstMapper.getAuditNodeConfig(version, nodeId);
            auditNodeRuleConfigs = auditNodeInstMapper.getAuditRuleConfig(version, nodeId);
            if (auditNodeConfigs != null && auditNodeRuleConfigs != null && auditNodeConfigs.size() > 0 && auditNodeRuleConfigs.size() > 0) {
                map = new HashMap();
                map.put("auditNodeConfigs", auditNodeConfigs);
                map.put("auditNodeRuleConfigs", auditNodeRuleConfigs);
            }
        } else {
            auditNodeConfigs = auditNodeInstMapper.getAuditRuleConfigVer(tenantId, version, nodeId);
            auditNodeRuleConfigVers = auditNodeInstMapper.getAuditNodeRuleConfigVer(tenantId, version, nodeId);
            if (auditNodeConfigs != null && auditNodeRuleConfigVers != null && auditNodeConfigs.size() > 0 && auditNodeRuleConfigVers.size() > 0) {
                map = new HashMap();
                map.put("auditNodeConfigs", auditNodeConfigs);
                map.put("auditNodeRuleConfigVers", auditNodeRuleConfigVers);
            }
        }
        return map;
    }
}
