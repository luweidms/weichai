package com.youming.youche.capital.provider.service;

import com.youming.youche.capital.api.ISysTenantDefService;
import com.youming.youche.capital.domain.SysTenantDef;
import com.youming.youche.capital.provider.mapper.SysTenantDefMapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.system.api.IUserDataInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;


/**
 * <p>
 * 车队表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-03-03
 */
@DubboService(version = "1.0.0")
public class SysTenantDefServiceImpl extends BaseServiceImpl<SysTenantDefMapper, SysTenantDef> implements ISysTenantDefService {


}
