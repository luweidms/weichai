package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Date:2022/1/10
 */
@Data
public class VehicleDataInfoVo implements Serializable {

    private String plateNumber;

    private Long loadEmptyOilCost;

    private Long loadFullOilCost;

    private Integer isUseCarOilCost;

    private Integer vehicleClass;

    private Long orgId;

    private Long userId;

    private Integer isAuth;

    private Integer authState;

    private Integer shareFlg;

    private Long linkUserId;

    private Long driverUserId;

    private Long copilotDriverId;

    private Date vehicleValidityTime;

    private Date operateValidityTime;

    private Long followDriverId;

    private Integer licenceType;

    private String vehicleLength;

    private Integer vehicleStatus;

    private String lightGoodsSquare;

    private String vehicleLoad;

    private String vinNo;

    private String operCerti;

    private String engineNo;

    private Long vehiclePicture;

    private String drivingLicense;

    private Long adriverLicenseCopy;

    private String drivingLicenseSn;

    private String etcCardNumber;

    private String equipmentCode;

    private Long specialOperCertFileId;

    private Long operCertiId;

    private Long tenantId;

    private String tenantName;

    private String linkMan;

    private String linkPhone;

    private Long vehicleCode;

    private String vehicleClassName;

    private String licenceTypeName;

    private String vehicleStatusName;

    private String vehicleLengthName;

    private String driverUserName;

    private String driverMobilePhone;

    private Integer driverCarUserType;

    private String  driverCarUserTypeName;

    private String tenantLinkMan;

    private String tenantLinkPhone;

    private String drivingLicenseOwner;

    private String brandModel;

    private String vehicleModel;

    private List<Map<String,Object>> vehicleOjbectLineArray;

}
