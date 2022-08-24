package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/5/17 14:52
 */
@Data
public class QueryUserOrderJurisdictionDto implements Serializable {

    /**
     * 订单收入权限
     */
    private Boolean hasOrderIncomePermission;

    /**
     * 订单成本权限
     */
    private Boolean hasOrderCostPermission;
}
