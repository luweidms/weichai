package com.youming.youche.order.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 订单校准里程表
 * </p>
 *
 * @author chenzhe
 * @since 2022-03-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderRetrographyCostInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;


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
     * 租户id
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

    @TableField(exist = false)
    private LocalDateTime checkTime;//里程校准时间
    @TableField(exist = false)
    private Long checkEmptyRunDistance;//校准空载里程
    @TableField(exist = false)
    private Long checkRunDistance;//校准载重里程

}
