package com.youming.youche.record.provider.service.impl.tenant;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.record.api.tenant.ITenantVehicleCertRelVerService;
import com.youming.youche.record.domain.tenant.TenantVehicleCertRelVer;
import com.youming.youche.record.provider.mapper.tenant.TenantVehicleCertRelVerMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * <p>
 * 车队与车辆证件版本表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@DubboService(version = "1.0.0")
public class TenantVehicleCertRelVerServiceImpl extends
		ServiceImpl<TenantVehicleCertRelVerMapper, TenantVehicleCertRelVer> implements ITenantVehicleCertRelVerService {

}
