package com.youming.youche.market.dto.youca;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProductNearByOutDto  implements Serializable {

    private static final long serialVersionUID = -5602482148142236278L;
    private Long oilId;
    private String oilName;
    private String quality;
    private String price;
    private String service;
    private Long originalPrice;
    private Long oilPrice;
    private String distance;
    private  Long distanceL;
    private String address;
    private String serviceCall;
    private String maxOil;
    private Integer isOrder;
    private Long tenantId;
    private Long serviceId;
    private String latitude;
    private String longitude;
    private String introduce;
    private String logoId;
    private String logoUrl;
    private Integer productType;

    private Integer localeBalanceState;
    private String localeBalanceName;
}
