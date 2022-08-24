package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;

@Data
public class OilRechargeAccountDetailsDto  implements Serializable {

    private static final long serialVersionUID = 166243328015135258L;

    private  Long orderId;
    private  Long noPayOil;

    /**
     * 车牌号
     */
    private String plateNumber;
    private  Double banlance;

    private  String vehicleClass;
    private  Long payeeUserid;
    private  String payee;
    private  String payeePhone;
    private  String payeeInfo;
}
