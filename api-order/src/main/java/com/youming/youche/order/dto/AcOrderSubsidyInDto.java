package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class AcOrderSubsidyInDto  implements Serializable {


    /************切换司机参数***************/
    private Long orderId ;//订单编号
    private Long driverUserId;//切换的司机id
    private String driverUserName;//副驾驶司机
    private Long driverSubsidy;//补贴 给切换司机增加金额
    private Long tenantId;//付款车队id
    private String oilAffiliation;//资金渠道(油)
    private String vehicleAffiliation;//资金渠道
    /************切换司机参数***************/


    private Long subjectId;//科目id
}
