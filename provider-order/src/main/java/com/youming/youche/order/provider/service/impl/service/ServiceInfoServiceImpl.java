package com.youming.youche.order.provider.service.impl.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.service.IServiceInfoService;
import com.youming.youche.order.domain.service.ServiceInfo;
import com.youming.youche.order.dto.AgentServiceDto;
import com.youming.youche.order.provider.mapper.service.ServiceInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 服务商表 服务实现类
 * </p>
 *
 * @author hzx
 * @since 2022-03-26
 */
@DubboService(version = "1.0.0")
@Service
public class ServiceInfoServiceImpl extends BaseServiceImpl<ServiceInfoMapper, ServiceInfo> implements IServiceInfoService {

    @Override
    public ServiceInfo getServiceInfoById(long userId) {
        return this.getById(userId);
    }

    @Override
    public AgentServiceDto getAgentService(long tenantId, int agentServiceType) {
        return baseMapper.getAgentService(tenantId, agentServiceType);
    }

    @Override
    public AgentServiceDto getAgentServiceByServiceId(long serviceId, int agentServiceType) {
        return baseMapper.getAgentServiceByServiceId(serviceId, agentServiceType);
    }

    @Override
    public ServiceInfo getServiceUserId(Long ServiceUserId) {
        LambdaQueryWrapper<ServiceInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceInfo::getServiceUserId,ServiceUserId);
        ServiceInfo serviceInfo = baseMapper.selectOne(wrapper);
        return serviceInfo;
    }

}
