package com.youming.youche.order.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 *司机补贴-历史表
 * </p> order_driver_subsidy_h
 *
 * @author liangyan
 * @since 2022-03-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderDriverSubsidyH extends BaseDomain {

    private static final long serialVersionUID = 1L;




    private Integer driverType; // '1 是 哦否',

    private Integer isCancle; //是否撤单

    private Integer isPayed; //是否支付， 1支付  0 未支付'

    private Long opId; //操作人

    private Long orderId;//订单号

    private Long subsidy; //补贴

    private LocalDate subsidyDate;//补贴时间

    private Long tenantId;//租户id

    private Long userId;//司机id


}
