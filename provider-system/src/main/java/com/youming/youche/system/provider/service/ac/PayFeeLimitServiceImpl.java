package com.youming.youche.system.provider.service.ac;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.system.api.ac.IPayFeeLimitService;
import com.youming.youche.system.domain.ac.PayFeeLimit;
import com.youming.youche.system.provider.mapper.ac.PayFeeLimitMapper;
import org.apache.dubbo.config.annotation.DubboService;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-02-19
 */
@DubboService(version = "1.0.0")
public class PayFeeLimitServiceImpl extends BaseServiceImpl<PayFeeLimitMapper, PayFeeLimit> implements IPayFeeLimitService {


}
