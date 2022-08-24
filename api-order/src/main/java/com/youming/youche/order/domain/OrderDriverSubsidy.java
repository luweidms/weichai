package com.youming.youche.order.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
* <p>
    * 
    * </p>
* @author liangyan
* @since 2022-03-12
*/
    @Data
    @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OrderDriverSubsidy extends BaseDomain {

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
            * 租户id
            */
    private Long tenantId;

            /**
            * 司机ID
            */
    private Long userId;
    @TableField(exist = false)
    private Integer verStatus;


}
