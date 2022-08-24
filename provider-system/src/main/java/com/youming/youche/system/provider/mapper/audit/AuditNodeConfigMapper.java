package com.youming.youche.system.provider.mapper.audit;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.system.domain.audit.AuditNodeConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <p>
 * 节点配置表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
public interface AuditNodeConfigMapper extends BaseMapper<AuditNodeConfig> {


    List<AuditNodeConfig> getFirstAuditNodeConfig(@Param("auditId") Long auditId,
                                                  @Param("tenantId") Long tenantId);

    Integer countId(@Param("version") Integer version,
                    @Param("nodeId") Long nodeId);

    List<AuditNodeConfig> getNextAuditNodeConfigList(@Param("nodeId") Long nodeId);

    /**
     * 检验审核版本是否最新版本
     *
     * @param version
     * @param nodeId
     * @return
     */
    int checkVersionNode(@Param("version") Integer version, @Param("nodeId") long nodeId);

    /**
     * 获取版本号
     */
    Integer getAuditRuleVersionNo(@Param("nodeId") Long nodeId);
}
