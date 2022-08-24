package com.youming.youche.record.provider.service.impl.tenant;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.record.api.trailer.ITenantTrailerRelService;
import com.youming.youche.record.domain.trailer.TenantTrailerRel;
import com.youming.youche.record.provider.mapper.trailer.TenantTrailerRelMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @version:
 * @Title: TenantTrailerRelServiceImpl
 * @Package: com.youming.youche.record.provider.service.impl.tenant
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/2/28 13:55
 * @company:
 */
@DubboService(version = "1.0.0")
public class TenantTrailerRelServiceImpl  extends ServiceImpl<TenantTrailerRelMapper, TenantTrailerRel> implements ITenantTrailerRelService {
}
