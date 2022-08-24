package com.youming.youche.market.provider.mapper.facilitator;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.market.domain.facilitator.TenantProductRel;
import com.youming.youche.market.dto.facilitator.TenantProductRelOutDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 租户与站点关系表Mapper接口
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-23
 */
public interface TenantProductRelMapper extends BaseMapper<TenantProductRel> {

    /**
     * 查询服务商下的所有站点
     *
     * @param serviceUserId
     * @param serviceType
     * @return
     */
   List<TenantProductRel> getServiceProductList(@Param("serviceUserId") Long serviceUserId,
                                                @Param("serviceType") Integer serviceType,
                                                @Param("tenantId") Long tenantId);


   List<TenantProductRelOutDto> getTenantProductList(@Param("tenantId") Long tenantId,@Param("list") List<Long> list);

}
