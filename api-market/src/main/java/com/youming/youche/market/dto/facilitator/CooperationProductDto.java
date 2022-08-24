package com.youming.youche.market.dto.facilitator;

import lombok.Data;

import java.io.Serializable;

@Data
public class CooperationProductDto implements Serializable {
    private Long productId; // 产品id
    private String tenantName; // 车队名称
    private String tenantPhone; // 租户名称(车队全称)
    private String tenantLinkMan; // 实际控制人
}
