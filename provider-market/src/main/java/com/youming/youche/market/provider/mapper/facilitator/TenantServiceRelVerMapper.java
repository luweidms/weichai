package com.youming.youche.market.provider.mapper.facilitator;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.market.domain.facilitator.TenantServiceRelVer;
import com.youming.youche.market.dto.facilitator.ServiceProductDto;
import com.youming.youche.market.dto.facilitator.TenantServiceDto;
import org.apache.ibatis.annotations.Param;


/**
 * <p>
 * 服务商与租户关系Mapper接口
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-25
 */
public interface TenantServiceRelVerMapper extends BaseMapper<TenantServiceRelVer> {
    /**
     * 保存
     * @param tenantServiceRelVer
     * @return
     */
    Long saveTenantServiceRelVer(TenantServiceRelVer tenantServiceRelVer);

    /**
     * 查询服务商历史档案
     * @return
     */
    IPage<TenantServiceDto> queryTenantServiceHis(Page<TenantServiceDto> page, @Param("isDel") Integer isDel, @Param("tenantId") Long tenantId, @Param("loginAcct") String loginAcct,
                                                  @Param("serviceName") String serviceName, @Param("linkman") String linkman,
                                                  @Param("serviceType") Integer serviceType);

    IPage<ServiceProductDto> queryProductHis(Page<ServiceProductDto> page, String productName, String serviceCall,
                                             String address, String serviceName, Long tenantId, Integer isDel);
}
