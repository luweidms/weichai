package com.youming.youche.order.provider.mapper.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.service.ServiceInfo;
import com.youming.youche.order.dto.AgentServiceDto;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 服务商表Mapper接口
 * </p>
 *
 * @author hzx
 * @since 2022-03-26
 */
public interface ServiceInfoMapper extends BaseMapper<ServiceInfo> {

    /**
     * 油品公司信息
     * @param tenantId
     * @param agentServiceType
     * @return
     */
    AgentServiceDto getAgentService(@Param("tenantId") long tenantId, @Param("agentServiceType") int agentServiceType);


    AgentServiceDto getAgentServiceByServiceId(@Param("serviceId") long serviceId,
                                               @Param("agentServiceType") int agentServiceType);
}
