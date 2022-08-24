package com.youming.youche.order.provider.service.order;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.ITenantAgentServiceRelService;
import com.youming.youche.order.domain.order.TenantAgentServiceRel;
import com.youming.youche.order.dto.SumQuotaAmtDto;
import com.youming.youche.order.provider.mapper.order.TenantAgentServiceRelMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@DubboService(version = "1.0.0")
@Service
public class TenantAgentServiceRelServiceImpl extends BaseServiceImpl<TenantAgentServiceRelMapper, TenantAgentServiceRel> implements ITenantAgentServiceRelService {
    @Resource
    private TenantAgentServiceRelMapper tenantAgentServiceRelMapper;

    @Override
    public SumQuotaAmtDto getSumQuotaAmtListByTenantId(Long tenantId, Integer agentServiceType) {
        return tenantAgentServiceRelMapper.getSumQuotaAmtListByTenantId(tenantId,agentServiceType);
    }
}
