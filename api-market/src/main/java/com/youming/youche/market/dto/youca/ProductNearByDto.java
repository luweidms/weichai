package com.youming.youche.market.dto.youca;

import com.youming.youche.market.domain.facilitator.OilPriceProvince;
import com.youming.youche.market.domain.facilitator.ServiceInfo;
import com.youming.youche.market.domain.facilitator.ServiceProduct;
import com.youming.youche.market.domain.facilitator.TenantProductRel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ProductNearByDto implements Serializable {
    private  Long id ;

    private List<ServiceProduct> serviceProduct;
    private  List<OilPriceProvince> oilPriceProvince;
    private List<TenantProductRel> tenantProductRel;
    private  List<ServiceInfo> serviceInfo;
}
