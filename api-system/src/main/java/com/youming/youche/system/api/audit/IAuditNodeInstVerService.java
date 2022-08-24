package com.youming.youche.system.api.audit;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.system.domain.audit.AuditNodeInstVer;

/**
 * <p>
 * 审核节点实例表 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
public interface IAuditNodeInstVerService extends IService<AuditNodeInstVer> {

    /**
     * 查询审核历史是否有深恶和记录
     *
     * @param busiCode 审核的业务编码
     * @param busiId   业务主键
     * @param tenantId 车队id
     * @return
     */
    Integer queryCountByMultipleConditions(String busiCode, Long busiId, Long tenantId);

}
