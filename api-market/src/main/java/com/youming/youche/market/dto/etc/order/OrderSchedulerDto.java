package com.youming.youche.market.dto.etc.order;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OrderSchedulerDto implements Serializable {

    private  Long orderId; //  订单号
    private  String vehicleCode; // 车辆id
    private  Long carDriverId;
    private Date carDependDate;
    private  Date carArriveDate;
    private  String  vehicleAffiliation; // 车辆归属枚举(对应 静态数据字段  capital_channel)',
    private  Long  toOrderId;
    private  Long  toTenantId;
    private  Date  dependTime;
}
