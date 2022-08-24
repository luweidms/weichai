package com.youming.youche.order.domain.order;

    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 订单油站分配表
    * </p>
* @author CaoYaJie
* @since 2022-03-17
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OrderOilDepotScheme extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 订单id
            */
    private Long orderId;

            /**
            * 油单价
            */
    private Long oilDepotPrice;

            /**
            * 油站id
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
            * 靠台距离  单位/m
            */
    private Long dependDistance;

            /**
            * 租户id
            */
    private Long tenantId;

            /**
            * 修改订单-油站id
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
            * 修改订单-靠台距离  单位/m
            */
    private Long dependDistanceUpdate;

            /**
            * 修改订单-加油升数
            */
    private Long oilDepotLitreUpdate;

            /**
            * 0不修改  1 需修改
            */
    private Integer isUpdate;


}
