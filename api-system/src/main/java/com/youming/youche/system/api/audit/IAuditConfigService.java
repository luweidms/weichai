package com.youming.youche.system.api.audit;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.system.domain.audit.AuditConfig;
import com.youming.youche.system.domain.audit.AuditNodeConfig;
import com.youming.youche.system.domain.audit.AuditNodeInst;

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

    AuditNodeInst queryAuditIng(String busiCode, Long busiId, Long tenantId);

    /**
     * 获取审核流程配置的第一个节点
     * @param auditId
     * @return
     */
    public AuditNodeConfig getFirstAuditNodeConfig(Long auditId, String token,Long tenantId);


    /**
     * 获取审核流程配置的第一个节点
     * @param auditId
     * @return
     */
    public AuditNodeConfig getFirstAuditNodeConfig(Long auditId, Long tenantId);


    /**
     * 获取版本号
     * @param nodeId
     * @return
     * @throws Exception
     */
    Integer getAuditRuleVersionNo(Long nodeId);

    /**
     * 租户初始化审核节点
     * @param tenantId
     */
    public void initAuditNode(Long tenantId,Long rootUserId,String token);


    /**
     * 查询业务是否已经有节点进行审核了
     * @param
     * @param busiId
     * @return
     *    true 表示有节点进行审核
     *    false 表示没有节点进行审核
     */
    public boolean isAuditIng(String busiCode,Long busiId,Long tenantId);


    /**
     * 校验节点的前置规则
     *
     * @param nodeId  节点主键
     * @param busiId  业务编码
     * @param params
     * @param isStart true开启审核，false 审核流程
     * @return
     * @throws Exception
     */
    public boolean checkNodePreRule(Long nodeId, Long busiId, Map params, Integer ruleVersion, Long tenantId,Boolean isStart) throws BusinessException;



    /**
     * 校验价格审核节点的前置规则
     * @param auditCode
     * @param busiId
     * @param params
     * @return
     *     key com.business.consts.AuditConsts.RULE_CODE  规则的编码
     *     value 是否满足规则
     *
     * @throws Exception
     */
    public Map<String,Boolean> checkNodePreRule(String auditCode, Long busiId,
                                                Map<String, Object> params, LoginInfo baseUser);

}
