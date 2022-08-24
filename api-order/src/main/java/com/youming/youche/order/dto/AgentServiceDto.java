package com.youming.youche.order.dto;

import com.youming.youche.order.domain.AgentServiceInfo;
import com.youming.youche.order.domain.order.TenantAgentServiceRel;
import com.youming.youche.order.domain.service.ServiceInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/4/28 9:46
 */
@Data
public class AgentServiceDto implements Serializable {

    private TenantAgentServiceRel tenantAgentServiceRel;

    private AgentServiceInfo agentServiceInfo;

    private ServiceInfo serviceInfo;
}
