package com.youming.youche.order.domain;

import com.youming.youche.order.domain.order.OrderAgingInfo;
import com.youming.youche.order.domain.order.OrderProblemInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PayForFinalChargeIn implements Serializable {
    private Long userId; //用户id
    private String vehicleAffiliation;  //资金渠道
    private Long finalFee; //尾款金额
    private Long insuranceFee;  //保费
    private List<OrderProblemInfo> problemInfos;//异常扣减对象集合
    private List<OrderAgingInfo> agingInfos;//时效罚款对象集合
    private Long orderId; //订单号
    private Long tenantId;  //租户id
    private Integer paymentDay; //尾款账期private Long userId; //用户id
    private String vehicle_affiliation;  //资金渠道
    private Long final_Fee; //尾款金额
    private Long insurance_fee;  //保费
    private List<OrderProblemInfo> problem_infos;//异常扣减对象集合
    private List<OrderAgingInfo> aging_infos;//时效罚款对象集合
    private Long Order_id; //订单号
    private Long tenant_Id;  //租户id
    private Integer payment_day; //尾款账期
}
