package com.youming.youche.market.provider.transfer;

import com.youming.youche.market.api.facilitator.ISysStaticDataMarketService;
import com.youming.youche.market.dto.facilitator.TenantServiceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.youming.youche.conts.EnumConsts.SysStaticData.SERVICE_BUSI_TYPE;

@Component
@RequiredArgsConstructor
public class TenantServiceTransfer {

    private final ISysStaticDataMarketService sysStaticDataService;

    public List<TenantServiceDto> getToTenantServiceVo(List<TenantServiceDto> tenantServiceDtos) throws Exception {
        if(tenantServiceDtos != null && tenantServiceDtos.size() > 0){

            for (TenantServiceDto dto : tenantServiceDtos) {
                String serviceType = sysStaticDataService.getSysStaticDataCodeName(SERVICE_BUSI_TYPE, dto.getServiceType()).getCodeName();
                dto.setServiceTypeName(serviceType);
            }

        }

        return tenantServiceDtos;
    }
}
