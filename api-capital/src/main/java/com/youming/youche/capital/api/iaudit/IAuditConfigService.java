package com.youming.youche.capital.api.iaudit;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.capital.domain.audit.AuditConfig;
import com.youming.youche.capital.domain.audit.AuditNodeConfig;
import com.youming.youche.capital.domain.audit.AuditNodeInst;

import java.util.Map;

/**
 * <p>
 * 审核配置主表 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
public interface IAuditConfigService extends IService<AuditConfig> {
    /**
     * 根据审核编码获取 审核配置对象
     * @param auditCode
     * @return
     */
    public AuditConfig getAuditConfigByCode(String auditCode);
    /**
     * 获取审核流程配置的第一个节点
     * @param auditId
     * @return
     */
    public AuditNodeConfig getFirstAuditNodeConfig(Long auditId, Long tenantId);


    /**
     * 查询业务是否处于待审核的状态
     * @param busiCode
     * @param busiId
     * @return
     */
    public AuditNodeInst queryAuditIng(String busiCode, Long busiId, Long tenantId);

    /**
     * 查询业务是否已经有节点进行审核了
     * @param busiCode
     * @param busiId
     * @return
     *    true 表示有节点进行审核
     *    false 表示没有节点进行审核
     */
    public boolean isAuditIng(String busiCode, Long busiId, Long tenantId);

    /**
     * 获取版本号
     * @param nodeId
     * @return
     * @throws Exception
     */
    Integer getAuditRuleVersionNo(Long nodeId) throws BusinessException;
    /**
     * 校验节点的前置规则
     *
     * @param nodeId  节点主键
     * @param busiId  业务编码
     * @param params
     * @return
     * @throws Exception
     */
    public boolean checkNodePreRule(Long nodeId, Long busiId, Map params, Integer ruleVersion, Long tenantId) throws BusinessException ;




}
