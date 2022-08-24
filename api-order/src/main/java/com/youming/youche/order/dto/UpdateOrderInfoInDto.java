package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateOrderInfoInDto  implements Serializable {
    /**
     * 备注
     */
    private String remark;
    private Long orgId;
    private Integer isNeedBill;
    /**
     * 始发省份
     */
    private Integer sourceProvince;
    private Integer sourceRegion;
    private Integer sourceCounty;
    private Integer desProvince;
    private Integer desRegion;
    private Integer desCounty;
    private String toTenantName;
    private Integer orderType;
    /**
     * 接单车队ID
     */
    private Long toTenantId;
}
