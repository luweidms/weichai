package com.youming.youche.order.api.service;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.service.ServiceInfo;
import com.youming.youche.order.dto.AgentServiceDto;

/**
 * <p>
 * 服务商表 服务类
 * </p>
 *
 * @author hzx
 * @since 2022-03-26
 */
public interface IServiceInfoService extends IBaseService<ServiceInfo> {

    /**
     * id获取服务商信息
     */
    ServiceInfo getServiceInfoById(long userId);

    /**
     * 油品公司信息
     * @param tenantId
     * @param agentServiceType
     * @return
     */
    AgentServiceDto getAgentService(long tenantId, int agentServiceType);


    /**
     * 根据id获取油品公司信息
     * @param serviceId
     * @param agentServiceType
     * @return
     */
    AgentServiceDto getAgentServiceByServiceId(long serviceId,int agentServiceType);

    /**
     * 需要返利的消费记录
     * @param ServiceUserId
     * @return
     */
    ServiceInfo getServiceUserId(Long ServiceUserId);
}
