package com.youming.youche.market.provider.service.facilitator;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.market.api.facilitator.IServiceSerialService;
import com.youming.youche.market.domain.facilitator.ServiceSerial;
import com.youming.youche.market.provider.mapper.facilitator.ServiceSerialMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-07-11
 */
@DubboService(version = "1.0.0")
@Service
public class ServiceSerialServiceImpl extends BaseServiceImpl<ServiceSerialMapper, ServiceSerial> implements IServiceSerialService {


}
