package com.youming.youche.order.domain;

    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author chenzhe
* @since 2022-03-25
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OrderStateTrackOper extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     *渠道类型
     */
    private String channelType;
    /**
     * 操作人id
     */
    private Long opId;

            /**
            * 业务变更类型
            */
    private Integer opType;

            /**
            * 订单编号
            */
    private Long orderId;

            /**
            * 业务变更类型
            */
    private Integer taskTrackSts;

            /**
            * 业务操作员编号
            */
    private Long updateOpId;

            /**
            * 车辆ID
            */
    private Long vehicleCode;


}
