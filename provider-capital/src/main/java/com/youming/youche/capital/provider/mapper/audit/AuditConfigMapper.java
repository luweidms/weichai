package com.youming.youche.capital.provider.mapper.audit;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.capital.domain.audit.AuditConfig;
import com.youming.youche.capital.vo.AuditNodeRuleConfigVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 审核配置主表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
public interface AuditConfigMapper extends BaseMapper<AuditConfig> {

    List<AuditConfig> getAuditConfigByCode(@Param("auditCode") String auditCode);

    Long getAuditingNodeNum(@Param("auditCode") String auditCode,
                            @Param("busiId") Long busiId,
                            @Param("status") Integer status);

    List<AuditNodeRuleConfigVo> getAuditNodeRuleConfigByNode(@Param("nodeId") Long nodeId);

    List<AuditNodeRuleConfigVo> getAuditNodeRuleConfigByNodeVer(@Param("nodeId") Long nodeId,
                                                                @Param("tenantId") Long tenantId,
                                                                @Param("version") Integer version);

}
