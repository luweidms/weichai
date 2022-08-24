package com.youming.youche.capital.provider.mapper.audit;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.capital.domain.audit.AuditNodeConfig;
import com.youming.youche.capital.domain.audit.AuditNodeInst;
import com.youming.youche.capital.domain.audit.AuditNodeRuleConfig;
import com.youming.youche.capital.domain.audit.AuditNodeRuleConfigVer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    List<AuditNodeConfig> getAuditNodeConfig(@Param("version") Integer version, @Param("nodeId") Long nodeId);

    List<AuditNodeRuleConfig> getAuditRuleConfig(@Param("version") Integer version, @Param("nodeId") Long nodeId);

    List<AuditNodeRuleConfigVer> getAuditNodeRuleConfigVer(@Param("tenantId") Long tenantId, @Param("version") Integer version, @Param("nodeId") Long nodeId);

    List<AuditNodeConfig> getAuditRuleConfigVer(@Param("tenantId") Long tenantId, @Param("version") Integer version, @Param("nodeId") Long nodeId);

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

    /**
     * 聂杰伟
     * 查询用户待审核的数据,
     * @param auditCode
     * @param userId
     * @param tenantId
     * @return
     */
    // TODO 待自测
    List<Long> getBusiIdByUserId(@Param("auditCode") String auditCode, @Param("userId") Long userId, @Param("tenantId") Long tenantId);
}
