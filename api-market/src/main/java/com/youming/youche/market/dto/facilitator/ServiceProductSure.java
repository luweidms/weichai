package com.youming.youche.market.dto.facilitator;

import lombok.Data;

import java.io.Serializable;

@Data
public class ServiceProductSure implements Serializable {
    private Long productId; // 服务商产品id
    private Boolean pass; // 启用或停用
    private String describe; // 描述
}
