package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class PayFeeLimitDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long tenantId; // 租户编号

    private BigDecimal value; // 配置值

}
