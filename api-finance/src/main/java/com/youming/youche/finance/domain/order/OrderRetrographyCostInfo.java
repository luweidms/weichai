package com.youming.youche.finance.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 订单校准里程表
 *
 * @author Terry
 * @since 2021-11-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderRetrographyCostInfo extends BaseDomain {

    /**
     * 主键
     */
    private Long id;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 总耗油量
     */
    private Long oilWearTotal;

    /**
     * 油费金额
     */
    private Long oilFeeTotal;

    /**
     * 订单油耗
     */
    private Long orderOilWear;

    /**
     * 空驶距离
     */
    private Long emptyRunDistance;

    /**
     * 行驶距离
     */
    private Long runDistance;

    /**
     * 订单总耗时
     */
    private Long orderTotalTime;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 油卡加油金额
     */
    private Long entityOilFee;

    /**
     * 定点加油金额
     */
    private Long virtualOilFee;

    /**
     * 油卡加油量
     */
    private Long entityOilLitre;

    /**
     * 定点加油量
     */
    private Long virtualOilLitre;

}
