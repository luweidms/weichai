package com.youming.youche.record.provider.service.impl.vehicle;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoVerService;
import com.youming.youche.record.common.SysStaticDataEnum;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.record.domain.vehicle.VehicleDataInfoVer;
import com.youming.youche.record.provider.mapper.vehicle.VehicleDataInfoVerMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 车辆版本表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@DubboService(version = "1.0.0")
public class VehicleDataInfoVerServiceImpl extends BaseServiceImpl<VehicleDataInfoVerMapper, VehicleDataInfoVer>
		implements IVehicleDataInfoVerService {

	@Autowired
	VehicleDataInfoVerMapper vehicleDataInfoVerMapper;

	@Lazy
	@Resource
	IVehicleDataInfoService iVehicleDataInfoService;


	@Override
	public VehicleDataInfoVer getVehicleDataInfoVer(String plateNumber, Long tenantId) {
		if (StringUtils.isBlank(plateNumber) && (tenantId == null || tenantId < 0)) {
			throw new BusinessException("参数错误，车牌号与租户不能同时为空");
		}
		QueryWrapper<VehicleDataInfoVer> queryWrapper=new QueryWrapper<>();
		if (tenantId != null && tenantId > 0) {
			queryWrapper.eq("TENANT_ID", tenantId);
		}
		queryWrapper.eq("PLATE_NUMBER", plateNumber);
		queryWrapper.orderByDesc("ID");
		List<VehicleDataInfoVer> list=vehicleDataInfoVerMapper.selectList(queryWrapper);
		if(list!=null && list.size()>0){
			return list.get(0);
		}
		return null;
	}

	@Override
	public VehicleDataInfoVer getVehicleDataInfoVer(Long vehicleCode) {
		QueryWrapper<VehicleDataInfoVer> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("vehicle_Code",vehicleCode)
				.eq("ver_State",SysStaticDataEnum.VER_STATE.VER_STATE_Y)
				.orderByDesc("id");
		List<VehicleDataInfoVer> list=vehicleDataInfoVerMapper.selectList(queryWrapper);
		if(list!=null && list.size()>0){
			return list.get(0);
		}
		return null;
	}

	@Override
	public VehicleDataInfoVer doAddVehicleDataInfoVerHis(Long vehicleCode) throws BusinessException {
		VehicleDataInfo vehicleDataInfo=iVehicleDataInfoService.getById(vehicleCode);
		if (null == vehicleDataInfo) {
			return null;
		}
		VehicleDataInfoVer vehicleDataInfoVer = new VehicleDataInfoVer();
		BeanUtil.copyProperties(vehicleDataInfo,vehicleDataInfoVer);
		vehicleDataInfoVer.setVehicleCode(vehicleCode);
		vehicleDataInfoVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_HIS);
		vehicleDataInfoVer.setCreateTime(LocalDateTime.now());
		vehicleDataInfoVer.setIsAuthSucc(3);
		Long id=this.saveById(vehicleDataInfoVer);
		vehicleDataInfoVer.setId(id);
		return vehicleDataInfoVer;
	}

	@Override
	public List<VehicleDataInfoVer> getVehicleObjectVer(Long vehicleCode, Long tenantId) {
		QueryWrapper<VehicleDataInfoVer> queryWrapper=new QueryWrapper<>();
		if (tenantId != null && tenantId > 0) {
			queryWrapper.eq("TENANT_ID", tenantId);
		}
		queryWrapper.eq("vehicle_Code", vehicleCode)
				.eq("ver_State",SysStaticDataEnum.VER_STATE.VER_STATE_Y);
		queryWrapper.orderByDesc("ID");
		return  baseMapper.selectList(queryWrapper);
	}

	private Long saveById(VehicleDataInfoVer vehicleDataInfoVer){
		super.save(vehicleDataInfoVer);
		return vehicleDataInfoVer.getId();
	}
}
