package com.youming.youche.finance.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 订单油卡表
 * </p>
 *
 * @author Terry
 * @since 2021-11-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderOilCardInfo extends BaseDomain {

    /**
     * 主键
     */
    private Long id;

    /**
     * 油卡号
     */
    private String oilCardNum;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 油费
     */
    private Long oilFee;

    /**
     * 油卡类型
     */
    private Integer cardType;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 油卡渠道 0 添加 1 车辆绑定
     */
    private Integer cardChannel;

}
