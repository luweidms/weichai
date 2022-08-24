package com.youming.youche.system.provider.mapper.audit;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.system.domain.audit.AuditNodeRuleConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 节点规则配置表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
public interface AuditNodeRuleConfigMapper extends BaseMapper<AuditNodeRuleConfig> {

    /**
     * 校验是否最新版本的规则
     */
    Integer checkVersionRule(@Param("nodeId") Long nodeId,
                             @Param("version") Integer version,
                             @Param("tenantId") Long tenantId);

    List<Map<Object, Object>> getAuditNodeRuleConfigByNodeVer(@Param("nodeId") Long nodeId);

    List<Map<Object, Object>> getAuditNodeRuleConfigByNodeVerFalse(@Param("nodeId") Long nodeId,
                                                                   @Param("tenantId") Long tenantId,
                                                                   @Param("version") Integer version);
}
