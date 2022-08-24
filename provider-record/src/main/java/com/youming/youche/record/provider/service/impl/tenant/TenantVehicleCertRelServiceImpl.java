package com.youming.youche.record.provider.service.impl.tenant;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.record.api.tenant.ITenantVehicleCertRelService;
import com.youming.youche.record.domain.tenant.TenantVehicleCertRel;
import com.youming.youche.record.provider.mapper.tenant.TenantVehicleCertRelMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * <p>
 * 车队与车辆证件表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@DubboService(version = "1.0.0")
public class TenantVehicleCertRelServiceImpl extends ServiceImpl<TenantVehicleCertRelMapper, TenantVehicleCertRel>
		implements ITenantVehicleCertRelService {

	@Override
	public TenantVehicleCertRel selectByRelId(Long relId) {
		LambdaQueryWrapper<TenantVehicleCertRel> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(TenantVehicleCertRel::getRelId,relId);
		return baseMapper.selectOne(wrapper);
	}

	@Override
	public List<TenantVehicleCertRel> findPlateNumber(String plateNumber) {
		return baseMapper.findPlateNumber(plateNumber);
	}
}
