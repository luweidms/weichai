package com.youming.youche.order.domain.order;

    import java.time.LocalDate;
    import java.time.LocalDateTime;

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
    public class OrderDriverSubsidyVer extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 司机类型，1主驾；2副驾；3切换司机
            */
    private Integer driverType;

            /**
            * 是否支付，1支付；0未支付
            */
    private Integer isPayed;

            /**
            * 操作人
            */
    private Long opId;

            /**
            * 订单号
            */
    private Long orderId;

            /**
            * 补贴
            */
    private Long subsidy;

            /**
            * 补贴时间
            */
    private LocalDateTime subsidyDate;

            /**
            * 租户ID
            */
    private Long tenantId;

            /**
            * 司机ID
            */
    private Long userId;

            /**
            * 版本状态，1已过期；0最新记录
            */
    private Integer verStatus;


}
