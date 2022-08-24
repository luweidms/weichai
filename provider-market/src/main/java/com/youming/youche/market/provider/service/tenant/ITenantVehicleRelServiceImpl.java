package com.youming.youche.market.provider.service.tenant;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.market.api.tenant.ITenantVehicleRelService;
import com.youming.youche.market.domain.tenant.TenantVehicleRel;
import com.youming.youche.market.provider.mapper.tenant.TenantVehicleRelMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * 车队与车辆关系表
 *
 * @author hzx
 * @date 2022/3/12 18:06
 */
@DubboService(version = "1.0.0")
public class ITenantVehicleRelServiceImpl extends BaseServiceImpl<TenantVehicleRelMapper, TenantVehicleRel> implements ITenantVehicleRelService {

    @Override
    public Long getZYCount(long vehicleCode, long tenantId, int vehicleClass) {
        LambdaQueryWrapper<TenantVehicleRel> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(TenantVehicleRel::getVehicleCode, vehicleCode);
        queryWrapper.eq(TenantVehicleRel::getTenantId, tenantId);
        LambdaQueryWrapper<TenantVehicleRel> eq = queryWrapper.eq(TenantVehicleRel::getVehicleClass, vehicleClass);

        if (vehicleClass != 1) { // 1 自有公司车
            eq.or().eq(TenantVehicleRel::getVehicleClass, 1);
        }

        Integer count = getBaseMapper().selectCount(queryWrapper);
        return count.longValue();
    }

}
