package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class OrderCostOtherTypeVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String typeName;
    private Long tenantId;
    private Long sortNum;
}
