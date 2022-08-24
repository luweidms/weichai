package com.youming.youche.market.dto.facilitator;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OilPriceProvinceDto extends BaseDomain {


    private static final long serialVersionUID = 1L;



    /**
     * 省份id
     */
    private Integer provinceId;

    /**
     * 油价
     */
    private Double oilPrice;

    /**
     * 省份名
     */
    private String provinceName;

    /**
     * 租户id
     */
    private Long tenantId;


}
