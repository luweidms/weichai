package com.youming.youche.record.provider.service.impl.tenant;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.record.api.vehicle.ITenantVehicleRelVerService;
import com.youming.youche.record.common.SysStaticDataEnum;
import com.youming.youche.record.domain.tenant.TenantVehicleRelVer;
import com.youming.youche.record.dto.tenant.TenantVehicleRelQueryDto;
import com.youming.youche.record.provider.mapper.tenant.TenantVehicleRelVerMapper;
import com.youming.youche.record.vo.tenant.TenantVehicleRelQueryVo;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * <p>
 * 车队与车辆关系版本表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@DubboService(version = "1.0.0")
public class TenantVehicleRelVerServiceImpl extends ServiceImpl<TenantVehicleRelVerMapper, TenantVehicleRelVer>
		implements ITenantVehicleRelVerService {



	@Override
	public TenantVehicleRelVer getTenantVehicleRelVer(Long vehicleCode) {
		QueryWrapper<TenantVehicleRelVer> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("vehicle_code",vehicleCode);
		List<TenantVehicleRelVer> list =baseMapper.selectList(queryWrapper);
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	@Override
	public List<TenantVehicleRelVer> getVehicleObjectVer(Long vehicleCode, Long tenantId) {
		QueryWrapper<TenantVehicleRelVer> queryWrapper=new QueryWrapper<>();
		if (tenantId != null && tenantId > 0) {
			queryWrapper.eq("TENANT_ID", tenantId);
		}
		queryWrapper.eq("vehicle_Code", vehicleCode)
				.eq("ver_State", SysStaticDataEnum.VER_STATE.VER_STATE_Y);
		queryWrapper.orderByDesc("ID");
		return  baseMapper.selectList(queryWrapper);
	}

	@Override
	public List<TenantVehicleRelQueryDto> doQueryVehicleSimpleInfoNoPage(TenantVehicleRelQueryVo tenantVehicleRelQueryVo) {
		return baseMapper.doQueryVehicleSimpleInfoNoPage(tenantVehicleRelQueryVo);
	}

	@Override
	public List<TenantVehicleRelQueryDto> doQueryBillReceiverNoPage(TenantVehicleRelQueryVo tenantVehicleRelQueryVo) {
		return baseMapper.doQueryBillReceiverNoPage(tenantVehicleRelQueryVo);
	}

	@Override
	public List<TenantVehicleRelVer> lists(Long id, Long tenantId, String beginDate, String endDate) {
		QueryWrapper<TenantVehicleRelVer> tenantVehicleRelVerQueryWrapper = new QueryWrapper<>();
		tenantVehicleRelVerQueryWrapper.eq("vehicle_code", id);
		tenantVehicleRelVerQueryWrapper.eq("tenant_id",tenantId);
		tenantVehicleRelVerQueryWrapper.between("create_time", beginDate, endDate);
		List<TenantVehicleRelVer> list = this.list(tenantVehicleRelVerQueryWrapper);
		return list;
	}
}
