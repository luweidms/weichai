package com.youming.youche.system.provider.mapper.audit;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.system.domain.audit.AuditNodeConfig;
import com.youming.youche.system.domain.audit.AuditNodeInst;
import com.youming.youche.system.domain.audit.AuditNodeRuleConfig;
import com.youming.youche.system.domain.audit.AuditNodeRuleConfigVer;
import com.youming.youche.system.domain.audit.AuditRuleConfig;
import com.youming.youche.system.domain.audit.AuditUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 审核节点实例表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */

public interface AuditNodeInstMapper extends BaseMapper<AuditNodeInst> {

    List<Integer> getAuditRuleVersionNo(@Param("id") Long id);

    Integer checkVersionRule(@Param("version") Integer version, @Param("nodeId") long nodeId, @Param("tenantId") Long tenantId);

    List<AuditNodeConfig> getAuditNodeConfig(@Param("version") Integer version, @Param("nodeId")Long nodeId);


    List<AuditNodeRuleConfig>  getAuditNodeRuleConfigData(@Param("nodeId") Long nodeId);

    List<AuditRuleConfig>  getAuditRuleConfigData(@Param("nodeId") Long nodeId);


    List<AuditRuleConfig>  getAuditRuleConfigDataInfo(@Param("tenantId") Long tenantid);


    List<AuditNodeRuleConfig> getAuditRuleConfigDatinfo(@Param("tenantId") Long tenantId, @Param("version") Integer version,@Param("nodeId")Long nodeId);

    List<AuditNodeRuleConfig> getAuditRuleConfig(@Param("version") Integer version, @Param("nodeId")Long nodeId);

    List<AuditNodeRuleConfigVer> getAuditNodeRuleConfigVer(@Param("tenantId") Long tenantId, @Param("version") Integer version, @Param("nodeId")Long nodeId);

    List<AuditNodeConfig> getAuditRuleConfigVer(@Param("tenantId") Long tenantId, @Param("version") Integer version,@Param("nodeId")Long nodeId);


    List<AuditRuleConfig> getAuditRuleConfigDatainfo(@Param("tenantId") Long tenantId, @Param("version") Integer version,@Param("nodeId")Long nodeId);

    Integer isAuditIng(@Param("auditCode") String auditCode,
                       @Param("tenantId") Long tenantId,
                       @Param("busiId") Long busiId);

    List<AuditNodeInst> queryAuditIng(@Param("auditCode") String auditCode,
                                      @Param("tenantId") Long tenantId,
                                      @Param("busiId") Long busiId);

    /**
     * 把某个业务的的具体的一个审核流程的所以实例数据更新为完成
     *
     * @param auditId
     * @param busiId
     */
    void updateAuditInstToFinish(@Param("auditId") Long auditId, @Param("busiId") Long busiId);

    List<AuditNodeInst> queryAuditNodeInst(@Param("auditCode") String auditCode,
                                           @Param("tenantId") Long tenantId,
                                           @Param("busiId") Long busiId);

    List<Long> getBusiIdByUserId(@Param("auditCode") String auditCode,@Param("userId") Long userId,
                                 @Param("tenantId") Long tenantId,@Param("pageSize") Integer pageSize,
                                 @Param("auditResult")Integer auditResult);

    /**
     * 审核记录查询（查询最新的一批次查询）
     */
    List<Map> getAuditNodeInstNew(@Param("auditCode") String auditCode, @Param("busiId") long busiId,
                                  @Param("tenantId") long tenantId, @Param("isDesc") boolean isDesc,
                                  @Param("state") String state);
    /**
     * 审核记录查询（查询最新的一批次查询）
     */
    List<Map> getAuditNodeInstNewVer(@Param("auditCode") String auditCode, @Param("busiId") long busiId,
                                  @Param("tenantId") long tenantId, @Param("isDesc") boolean isDesc,
                                  @Param("state") String state);

    /**
     * 批量查询业务是否处于待审核的状态
     * @param busiCode
     * @param busiIds
     * @return
     */
    List<AuditNodeInst> queryAuditIngs(@Param("busiCode") String busiCode,
                                      @Param("busiIds") List<Long> busiIds);

    /**
     * 根据节点ID、目标用户、类型查询节点审批人
     * @param nodeId
     * @param targetObjId
     * @param targetObjType
     * @return
     */
    AuditUser getAuditUser(@Param("nodeId") Long nodeId,
                           @Param("targetObjId") Long targetObjId,
                           @Param("targetObjType") Integer targetObjType);

}
