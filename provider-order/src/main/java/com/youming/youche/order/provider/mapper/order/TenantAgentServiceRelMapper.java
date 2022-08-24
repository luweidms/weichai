package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.order.TenantAgentServiceRel;
import com.youming.youche.order.dto.SumQuotaAmtDto;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper接口
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
public interface TenantAgentServiceRelMapper extends BaseMapper<TenantAgentServiceRel> {


    SumQuotaAmtDto getSumQuotaAmtListByTenantId(@Param("tenantId") Long tenantId,@Param("agentServiceType") Integer agentServiceType);
}
