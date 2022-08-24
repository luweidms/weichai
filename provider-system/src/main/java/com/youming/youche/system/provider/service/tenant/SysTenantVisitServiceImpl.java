package com.youming.youche.system.provider.service.tenant;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.system.api.tenant.ISysTenantVisitService;
import com.youming.youche.system.domain.tenant.SysTenantVisit;
import com.youming.youche.system.provider.mapper.tenant.SysTenantVisitMapper;
import org.apache.dubbo.config.annotation.DubboService;


/**
 * <p>
 * 访客档案 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-01-18
 */
@DubboService(version = "1.0.0")
public class SysTenantVisitServiceImpl extends BaseServiceImpl<SysTenantVisitMapper, SysTenantVisit> implements ISysTenantVisitService {


}
