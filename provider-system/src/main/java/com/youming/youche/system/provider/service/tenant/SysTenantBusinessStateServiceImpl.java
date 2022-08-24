package com.youming.youche.system.provider.service.tenant;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.system.api.tenant.ISysTenantBusinessStateService;
import com.youming.youche.system.domain.tenant.SysTenantBusinessState;
import com.youming.youche.system.provider.mapper.tenant.SysTenantBusinessStateMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * <p>
 * 车队的开票设置 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-01-09
 */
@DubboService(version = "1.0.0")
public class SysTenantBusinessStateServiceImpl
        extends BaseServiceImpl<SysTenantBusinessStateMapper, SysTenantBusinessState>
        implements ISysTenantBusinessStateService {

    @Resource
    SysTenantBusinessStateMapper sysTenantBusinessStateMapper;

    @Override
    public int correlateVisitRecord(long tenantId, long visitId) {
        return sysTenantBusinessStateMapper.correlateVisitRecord(tenantId, visitId);
    }

    @Override
    public SysTenantBusinessState queryByTenantId(Long tenantId) {
        LambdaQueryWrapper<SysTenantBusinessState> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysTenantBusinessState::getTenantId, tenantId);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public int updateBusinessState(SysTenantBusinessState sysTenantBusinessState, Long tenantId) {
        int i = sysTenantBusinessStateMapper.updateBusinessState(sysTenantBusinessState, tenantId);
        return i;
    }


}
