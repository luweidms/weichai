package com.youming.youche.system.provider.service.audit;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.system.api.audit.IAuditNodeInstVerService;
import com.youming.youche.system.domain.audit.AuditNodeInstVer;
import com.youming.youche.system.provider.mapper.audit.AuditNodeInstVerMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * <p>
 * 审核节点实例表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
@DubboService(version = "1.0.0")
public class AuditNodeInstVerServiceImpl extends ServiceImpl<AuditNodeInstVerMapper, AuditNodeInstVer>
		implements IAuditNodeInstVerService {

    @Override
    public Integer queryCountByMultipleConditions(String busiCode, Long busiId, Long tenantId) {
        LambdaQueryWrapper<AuditNodeInstVer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AuditNodeInstVer::getAuditCode, busiCode);
        queryWrapper.eq(AuditNodeInstVer::getBusiId, busiId);
        queryWrapper.eq(AuditNodeInstVer::getTenantId, tenantId);
        return this.count(queryWrapper);
    }
}
