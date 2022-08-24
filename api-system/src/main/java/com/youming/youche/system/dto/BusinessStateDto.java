package com.youming.youche.system.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zengwen
 * @date 2022/4/24 13:07
 */
@Data
public class BusinessStateDto implements Serializable {

    private Long businessStateId;
    private Long visitId;
    private Long tenantId;
    private Integer tenantType;
    private Double annualTurnover;
    private String customerType;
    private String otherCustomerType;
    private String vehicleType;
    private Integer ownVehicleNumber;
    private Integer businessVehicleNumber;
    private Integer attachedVehicleNumber;
    private Integer otherVehicleUsed;
    private Integer staffNumber;
    private Integer vehicleManageNumber;
    private Integer dispatcherNumber;
    private Integer customerServiceNumber;
    private Integer financialNumber;
    private String systemUser;
    private String systemPhone;
    private String infoUser;
    private String infoPhone;
    private String openOrderUser;
    private String openOrderPhone;
    private String financialUser;
    private String financialPhone;
    private String serviceUser;
    private String servicePhone;
    private Double mainVehiclePercent;
    private Double stopPercent;
    private Double distributPercent;
    private Double retailPercent;
    private Double petroChinaPercent;
    private Double threePartyPercent;
    private Double purchasePercent;
    private Double externalPercent;
    private Double afterpayPercent;
    private Double prepayPercent;
    private Double cashPercent;
    private String issuers;
    private String otherIssuers;
    private String ownFreightType;
    private String otherFreightType;
    private String systemUsed;
    private String otherSystemUsed;
    private String hardwareUsed;
    private String otherHardwareUsed;
    private Date createDate;

    private String tenantTypeName;
    private String customerTypeString;
    private String vehicleTypeString;
    private String issuersString;
    private String ownFreightTypeString;
    private String otherFreightTypeString;
    private String systemUsedString;
    private String hardwareUsedString;

}
