package com.youming.youche.system.api.audit;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.system.domain.audit.AuditNodeConfigVer;

import java.util.List;

/**
 * <p>
 * 节点配置版本表 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
public interface IAuditNodeConfigVerService extends IService<AuditNodeConfigVer> {

    /**
     * 是否已经存在该版本节点信息了
     * @param nodeIds
     * @param tenantId
     * @param version
     * @param auditId
     * @return
     * @author qiulf
     */
    public int checkNodeVer(List<Long> nodeIds, long tenantId, Integer version, long auditId);

    /**
     * 获取历史版本数据
     * @param version
     * @param nodeId
     * @return
     */
    AuditNodeConfigVer getAuditNodeConfigVer(Integer version,long nodeId,boolean isParent,Long tenantId);

    AuditNodeConfigVer getAuditNodeConfigVerIsTrue(Integer version, Long nodeId, Long tenantId);

    AuditNodeConfigVer getAuditNodeConfigVerIsFalse(Integer version, Long nodeId, Long tenantId);
}
