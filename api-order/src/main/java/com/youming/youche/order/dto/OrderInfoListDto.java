package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: luona
 * @date: 2022/3/25
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class OrderInfoListDto implements Serializable {
    private Long orderId;//订单id
    private Integer orderStateCode;//订单状态编码
    private String orderState;//订单状态
    private String orderType;//订单类型
    private Integer paymentWay;//自由车付款方式
    private Float arriveTime;//到达时限;
    private Integer isTransit;//是否外发
    private LocalDateTime dependTime;//靠台时间
    private String sourceName;//线路名称
    private String carDriverPhone;//司机手机
    private String carDriverMan;//司机姓名


    private boolean isAddProblem;//是否能添加异常
    private Long receiveTenantId;
    private String receiveTenantName;
    private Long fromTenantId;//来源租户id
    private String fromTenantName;
    private String orderTypeName;//订单类型



}
