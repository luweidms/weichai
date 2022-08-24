package com.youming.youche.capital.provider.mapper.audit;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.capital.domain.audit.AuditNodeConfigVer;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <p>
 * 节点配置版本表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2022-01-06
 */
public interface AuditNodeConfigVerMapper extends BaseMapper<AuditNodeConfigVer> {

    List<AuditNodeConfigVer> getAuditNodeConfigVerList(@Param("nodeId") Long nodeId,
                                                       @Param("tenantId") Long tenantId,
                                                       @Param("version") Integer version);

    /**
     * 获取历史版本数据
     *
     * @param version
     * @param nodeId
     * @return
     */
    AuditNodeConfigVer getAuditNodeConfigVer(@Param("version") Integer version, @Param("nodeId") long nodeId, @Param("tenantId") Long tenantId, @Param("isParent") String isParent);

    AuditNodeConfigVer getAuditNodeConfigVerIsTrue(@Param("version") Integer version,
                                                   @Param("nodeId") Long nodeId,
                                                   @Param("tenantId") Long tenantId);

    AuditNodeConfigVer getAuditNodeConfigVerIsFalse(@Param("version") Integer version,
                                                    @Param("nodeId") Long nodeId,
                                                    @Param("tenantId") Long tenantId);

}
