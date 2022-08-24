package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderDriverSubsidyDto implements Serializable {

    private Long orderId; //订单号
    private Long userId;//用户id
    private String userPhone; //  联系人电话
    private String userName;  // 联系人姓名
    private Long subsidy; //补贴
}
