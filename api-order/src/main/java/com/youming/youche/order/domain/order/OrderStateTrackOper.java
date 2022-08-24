package com.youming.youche.order.domain.order;

    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author liangyan
* @since 2022-03-23
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OrderStateTrackOper extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 渠道类型
     */
    private String channelType;
    /**
     * 操作人
     */
    private Long opId;
    /**
     * 业务变更类型
     */
    private Integer opType;
    /**
     * 订单号
     */
    private Long orderId;
    /**
     * 业务变更类型
     */
    private Integer taskTrackSts;
    /**
     * 修改操作员id
     */
    private Long updateOpId;
    /**
     * 车辆id
     */
    private Long vehicleCode;


}
