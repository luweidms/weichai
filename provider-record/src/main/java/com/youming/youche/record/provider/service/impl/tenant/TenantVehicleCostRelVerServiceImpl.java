package com.youming.youche.record.provider.service.impl.tenant;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.record.api.tenant.ITenantVehicleCostRelVerService;
import com.youming.youche.record.domain.tenant.TenantVehicleCostRelVer;
import com.youming.youche.record.provider.mapper.tenant.TenantVehicleCostRelVerMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @version:
 * @Title: TenantVehicleCostRelVerServiceImpl
 * @Package: com.youming.youche.record.provider.service.impl.tenant
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/1/7 11:27
 * @company:
 */
@DubboService(version = "1.0.0")
public class TenantVehicleCostRelVerServiceImpl  extends ServiceImpl<TenantVehicleCostRelVerMapper, TenantVehicleCostRelVer>
        implements ITenantVehicleCostRelVerService {
}
