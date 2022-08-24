package com.youming.youche.system.provider.service;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.system.api.ISysMenuUrlService;
import com.youming.youche.system.domain.SysMenuUrl;
import com.youming.youche.system.provider.mapper.SysMenuUrlMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * <p>
 * 角色权限表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
@DubboService(version = "1.0.0")
public class SysMenuUrlServiceImpl extends BaseServiceImpl<SysMenuUrlMapper, SysMenuUrl> implements ISysMenuUrlService {

}
