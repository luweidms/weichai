package com.youming.youche.record.dto.order;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OrderSchedulerDto implements Serializable {
  private  Long orderId; //  订单号
  private  String vehicleCode; // 车辆编号
  private  Long carDriverId; // 司机id
  private  Date  carDependDate;// 实际靠台时间
  private  Date carArriveDate; // 实际到达时间
  private  String  vehicleAffiliation; // 车辆归属枚举(对应 静态数据字段  capital_channel)',
  private  Long  toOrderId;
  private  Long  toTenantId;
  private  Date  dependTime; // 靠太时间
  private   Integer vehicleClass; // 车辆类型
}
