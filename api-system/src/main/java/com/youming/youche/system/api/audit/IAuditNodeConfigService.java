package com.youming.youche.system.api.audit;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.system.domain.audit.AuditNodeConfig;

import java.util.List;

/**
 * <p>
 * 节点配置表 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
public interface IAuditNodeConfigService extends IService<AuditNodeConfig> {

    /**
     * 保存版本记录,返回最新版本号
     * @author qiulf
     */
    public int saveAuditNodeConfigVer(Long auditId,Long tenantId,Long userId)throws Exception;

    public List<AuditNodeConfig> getAuditNodeConfigByParentId(Long auditId, Long parentNodeId, Long tenantId);

    public List<AuditNodeConfig> getAuditNodeConfigList(long auditId,long tenantId);

    /**
     * 审核节点
     * @param nodeId
     * @return
     */
    public AuditNodeConfig getAuditNodeConfig(long nodeId);

    /**
     * 通过夫级节点查询
     * @param parentNodeId
     * @return
     */
    public AuditNodeConfig getAuditNodeByParentId(long parentNodeId);


    /**
     * 检验审核版本是否最新版本
     * @param version
     * @param nodeId
     * @return
     */
    boolean checkVersionNode(Integer version,long nodeId);

    /**
     * 获取下个节点的信息
     * @param nodeId  当前的节点ID
     * @return
     *     如果没有下个节点，返回null
     */
    public AuditNodeConfig getNextAuditNodeConfig(Long nodeId);

    /**
     * 获取下个节点的信息
     * @param nodeId  当前的节点ID
     * @return
     *     如果没有下个节点，返回null
     */
    public AuditNodeConfig getNextAuditNodeConfig(Long nodeId,List<AuditNodeConfig> auditConfigs);

    Integer countId(Integer version, Long nodeId);

    /**
     * 根据节点id查询列表
     * */
    List<AuditNodeConfig> getNextAuditNodeConfigList(Long nodeId);

    /**
     * 方法实现说明 查询配置表所有信息
     * @author      terry
     * @param
     * @return      java.util.List<com.youming.youche.system.domain.audit.AuditNodeConfig>
     * @exception
     * @date        2022/5/30 22:11
     */
    List<AuditNodeConfig> getAll();
}
