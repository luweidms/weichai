package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class OilCardSaveInDto  implements Serializable {
    private Long cardId;
    private String oilCarNum;
    private Integer oilCardStatus;
    private Long userId;
    private Integer cardType;
    private Long cardBalance;
    private Integer isFleet;
    private Long tenantId;

    private Long opId;
    private String opName;

    private String vehicleNumberStr;

    private String cardTypeName;

    private String serviceName;

    private Boolean isSkipVerify;
}
