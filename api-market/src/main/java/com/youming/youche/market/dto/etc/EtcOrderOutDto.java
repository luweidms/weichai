package com.youming.youche.market.dto.etc;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class EtcOrderOutDto implements Serializable {

    private static final long serialVersionUID = 8829214302968107420L;
    private Long carDriverId;
    private Long orderId;
    private String vehicleAffiliation;
    private String etcCardNumber;
    private String plateNumber;
    private String carPhone;
    private Long toTenantId;
    private String oilAffiliation;
    private Long vehicleCode;

    private LocalDateTime carDependDate;
    private  LocalDateTime carArriveDate;
    private  Long toOrderId;
    private   LocalDateTime dependTime;
    private Integer paymentWay;

}
