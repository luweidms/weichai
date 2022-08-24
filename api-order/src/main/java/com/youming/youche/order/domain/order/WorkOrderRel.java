package com.youming.youche.order.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class WorkOrderRel extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private Long orderId;
    /**
     * 车队id
     */
    private Long tenantId;
    /**
     * 工单ID
     */
    private Long workId;


}
