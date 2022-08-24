package com.youming.youche.order.domain.transit;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 客户停经点
 * </p>
 *
 * @author hzx
 * @since 2022-03-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TransitCust extends BaseDomain {

    /**
     * 经停点id
     */
    private Long transitLineId;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 客户编码
     */
    private String customerCode;

    /**
     * 公司名称(全称)
     */
    private String companyName;

    /**
     * 租户id
     */
    private Long tenantId;


}
