package com.youming.youche.order.domain.order;

    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
 * <p>
 * 订单变更操作记录表
 * </p>
 * @author liangyan
 * @since 2022-03-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderOpRecord extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 订单编号
     */
    private Long orderId;

    /**
     * 业务变更类型
     */
    private Integer opType;

    /**
     * 租户编号
     */
    private Long tenantId;

    /**
     * 业务操作员编号
     */
    private Long opId;
    /**
     *渠道类型
     */
    private String channelType;

    /**
     * 业务操作员编号
     */
    private Long updateOpId;


}
