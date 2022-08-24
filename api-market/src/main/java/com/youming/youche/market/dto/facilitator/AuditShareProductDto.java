package com.youming.youche.market.dto.facilitator;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuditShareProductDto implements Serializable {
    private Long productId; // 服务商产品id
    private Integer authState; // 审核状态
    private String authRemark; // 描述
}
