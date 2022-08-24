package com.youming.youche.record.provider.service.impl.tenant;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.record.api.tenant.ITenantVehicleCostRelService;
import com.youming.youche.record.domain.tenant.TenantVehicleCostRel;
import com.youming.youche.record.provider.mapper.tenant.TenantVehicleCostRelMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * @version:
 * @Title: TenantVehicleCostRelServiceImpl
 * @Package: com.youming.youche.record.provider.service.impl.tenant
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/1/6 17:05
 * @company:
 */
@DubboService(version = "1.0.0")
public class TenantVehicleCostRelServiceImpl extends ServiceImpl<TenantVehicleCostRelMapper, TenantVehicleCostRel>

        implements ITenantVehicleCostRelService {
    @Override
    public TenantVehicleCostRel selectByRelId(Long relId) {
        LambdaQueryWrapper<TenantVehicleCostRel> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(TenantVehicleCostRel::getRelId,relId);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public TenantVehicleCostRel getTenantVehicleCostRel(String plateNumber, Long tenantId) {
        LambdaQueryWrapper<TenantVehicleCostRel> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(TenantVehicleCostRel::getPlateNumber, plateNumber);
        wrapper.eq(TenantVehicleCostRel::getTenantId, tenantId);
        wrapper.last("limit 1");
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public List<TenantVehicleCostRel> lists(Long id, Long tenantId, String beginDate, String endDate) {
        QueryWrapper<TenantVehicleCostRel> tenantVehicleCostRelQueryWrapper = new QueryWrapper<>();
        tenantVehicleCostRelQueryWrapper.eq("vehicle_code", id);
        tenantVehicleCostRelQueryWrapper.eq("tenant_id",tenantId);
        tenantVehicleCostRelQueryWrapper.between("create_time", beginDate, endDate);
        List<TenantVehicleCostRel> list = this.list(tenantVehicleCostRelQueryWrapper);
        return list;
    }
}
