package com.youming.youche.order.domain.transit;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 运输货物表
 * </p>
 *
 * @author hzx
 * @since 2022-03-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TransitGoods extends BaseDomain {

    /**
     * 客户id
     */
    private Long custId;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 货物名称
     */
    private String goodsName;

    /**
     * 货物规格
     */
    private String goodsFeature1;

    /**
     * 货物规格
     */
    private String goodsFeature2;

    /**
     * 货物体积
     */
    private Float square;

    /**
     * 货物件数
     */
    private Integer nums;

    /**
     * 货物重量kg
     */
    private Float weight;

    /**
     * 租户id
     */
    private Long tenantId;


}
