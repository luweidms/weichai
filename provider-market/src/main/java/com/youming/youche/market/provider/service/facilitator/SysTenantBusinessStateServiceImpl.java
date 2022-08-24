package com.youming.youche.market.provider.service.facilitator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.market.api.facilitator.ISysTenantBusinessStateService;
import com.youming.youche.market.domain.facilitator.SysTenantBusinessState;
import com.youming.youche.market.provider.mapper.facilitator.SysTenantBusinessStateMapper;
import org.apache.dubbo.config.annotation.DubboService;


/**
 * <p>
 * 车队经营状况 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-14
 */
@DubboService(version = "1.0.0")
public class SysTenantBusinessStateServiceImpl extends BaseServiceImpl<SysTenantBusinessStateMapper, SysTenantBusinessState> implements ISysTenantBusinessStateService {


    @Override
    public SysTenantBusinessState queryByTenantId(Long tenantId) {
        if (null == tenantId) {
            return null;
        }
        LambdaQueryWrapper<SysTenantBusinessState> lambda= new QueryWrapper<SysTenantBusinessState>().lambda();
        lambda.eq(SysTenantBusinessState::getTenantId,tenantId);
        return this.getOne(lambda);
    }
}
