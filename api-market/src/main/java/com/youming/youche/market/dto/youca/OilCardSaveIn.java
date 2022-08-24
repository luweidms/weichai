package com.youming.youche.market.dto.youca;

import lombok.Data;

import java.io.Serializable;
@Data
public class OilCardSaveIn implements Serializable {
    private Long id;
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
    /**失败原因*/
    private String reasonFailure;

}
