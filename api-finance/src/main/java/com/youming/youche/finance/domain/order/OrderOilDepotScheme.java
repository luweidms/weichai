package com.youming.youche.finance.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 订单油站分配表
 *
 * @author Terry
 * @since 2021-11-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderOilDepotScheme extends BaseDomain {

    /**
     * 主键
     */
    private Long schemeId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 油单价
     */
    private Long oilDepotPrice;

    /**
     * 油站ID
     */
    private Long oilDepotId;

    /**
     * 油站名字
     */
    private String oilDepotName;

    /**
     * 加油费用
     */
    private Long oilDepotFee;

    /**
     * 加油升数
     */
    private Long oilDepotLitre;

    /**
     * 油站纬度
     */
    private String oilDepotNand;

    /**
     * 油站经度
     */
    private String oilDepotEand;

    /**
     * 靠台距离 单位/m
     */
    private Long dependDistance;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 修改订单-油站ID
     */
    private Long oilDepotIdUpdate;

    /**
     * 修改订单-油单价
     */
    private Long oilDepotPriceUpdate;

    /**
     * 修改订单-油站名字
     */
    private String oilDepotNameUpdate;

    /**
     * 修改订单-加油费用
     */
    private Long oilDepotFeeUpdate;

    /**
     * 修改订单-油站纬度
     */
    private String oilDepotNandUpdate;

    /**
     * 修改订单-油站经度
     */
    private String oilDepotEandUpdate;

    /**
     * 修改订单-靠台距离 单位/m
     */
    private Long dependDistanceUpdate;

    /**
     * 修改订单-加油升数
     */
    private Long oilDepotLitreUpdate;

    /**
     * 0不修改 1 需修改
     */
    private Integer isUpdate;

}
