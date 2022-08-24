package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderCostDetailReportDto implements Serializable {

    private static final long serialVersionUID = -4153941041572744033L;
    private String id;
    private String  sourceRegionName;
    private String  desRegionName;
    private String  orderStateName;
    private Boolean  isDoSave;
    private OrderCostReportDto orderCostReportDto;
    private Integer state;
    //车牌号
    private String plateNumber;
    //司机手机号
    private String driverPhone;
    //司机姓名
    private String carDriverMan;


}
