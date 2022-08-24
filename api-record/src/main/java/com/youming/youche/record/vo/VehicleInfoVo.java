package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * @Date:2022/1/11
 */
@Data
public class VehicleInfoVo implements Serializable {


    private static final long serialVersionUID = -5754088877443891144L;

    private Integer autoAudit;

    private String manageFee;

    private String managementCost;

    /**
     * 车牌号码
     */
    private String plateNumber;

    /**
     * 车辆类型
     */
    private Integer vehicleClass;

    /**
     * 牌照类型(1:整车，2：拖头)
     */
    private Integer licenceType;

    /**
     * 车架号
     */
    private String vinNo;

    /**
     * 发动机号
     */
    private String engineNo;

    /**
     * 使用车辆油耗
     */
    private Integer isUseCarOilCost;

    /**
     *
     */
    private String drivingLicenseOwner;

    private Integer shareFlg;

    private Long orgId;

    private Long driverUserId;

    private String driverUserName;

    private String driverUserPhone;

    private String memberTypeName;

    private Long copilotDriverId;

    private String copilotDriverName;

    private String copilotDriverPhone;

    private String copilotDriverTypeName;

    private Long followDriverId;

    private String followDriverName;

    private String followDriverPhone;

    private String followDriverTypeName;

    private Long userId;

    private String userName;

    private Long linkUserId;

    private String linkUserName;

    private String contactsPhone;

    private String billReceiverMobile;

    private String billReceiverName;

    private Long billReceiverUserId;

    private Long residual;

    private String maintainWarnDis;

    private Long vehiclePicture;

    private String vehiclePicUrl;

    private Long operCertiId;

    private String operCerti;

    private String operCertiUrl;

    private Long drivingLicense;

    private String drivingLicenseUrl;

    private Long adriverLicenseCopy;

    private String adriverLicenseCopyUrl;

    private Long specialOperCertFileId;

    private String specialOperCertFileUrl;

    private List<VehicleObjectLineVo> vehicleOjbectLineArray;

    private Long adminUser;
    private String loadEmptyOilCost;

    private String loadFullOilCost;

    private String brandModel;

    private Long price;

    private String loanInterest;

    private Integer interestPeriods;

    private Integer payInterestPeriods;

    private Date purchaseDate;

    private String depreciatedMonth;

    private String collectionInsurance;

    private String examVehicleFee;

    private String maintainFee;

    private String repairFee;

    private String tyreFee;

    private String otherFee;

    private String maintainDis;

    private Date prevMaintainTime;

    private Date registrationTime;

    private String registrationNumble;

    private Date annualVeriTime;

    private Date annualVeriTimeEnd;

    private LocalDate operateValidityTimeBegin;

    private LocalDate operateValidityTime;

    private Date busiInsuranceTime;

    private Date busiInsuranceTimeEnd;

    private Date insuranceTime;

    private Date insuranceTimeEnd;

    private Date otherInsuranceTime;

    private Date otherInsuranceTimeEnd;

    private Date seasonalVeriTime;

    private Date seasonalVeriTimeEnd;

    private LocalDate vehicleValidityTimeBegin;

    private LocalDate vehicleValidityTime;

    private String busiInsuranceCode;

    private String insuranceCode;

    private String otherInsuranceCode;

    private List<VehicleLineRelsVo> vehicleLineRels;

    /**
     * 车辆型号
     */
    private String vehicleModel;


    /**
     * 车辆租赁协议
     */
    private Long rentAgreementId;
    /**
     * 车辆租赁协议url
     */
    private String rentAgreementUrl;
    /**
     * 招商合同文件图片
     */
    private String investContractFilePicUrl;

    /**
     * 招商合同文件id
     */
    private Long investContractFilePic;

    private Long relId;

    private Integer authState;

    private String attachContractFileicattach;

    private String vehicleLength;

    private Integer vehicleStatus;

    private String lightGoodsSquare;

    private String vehicleLoad;

    private String drivingLicenseSn;

    private String etcCardNumber;

    private Integer equipmentCode;

    /**
     * 车队id
     */
    private Long tenantId;

    /**
     * 车队名称
     */
    private String tenantName;

    /**
     * 车队联系人
     */
    private String linkMan;

    /**
     * 车队电话
     */
    private String linkPhone;

    /**
     * 司机姓名
     */
    private String driverName;

    /**
     * 司机手机
     */
    private String driverPhone;

    private String driverMobilePhone;

    /**
     * 车辆id
     */
    private Long vehicleCode;

    /**
     * 挂靠合同id
     */
    private Long attachId;

    /**
     * 挂靠合同url
     */
    private String attachUrl;

    private Integer isOwnCar;

    private Boolean otherOwnCar;

    private String vehicleLengthName;

    private String vehicleClassName;

    private String licenceTypeName;

    private String vehicleStatusName;

    private String authStateName;

    private Integer driverCarUserType;

    private String driverCarUserTypeName;

    private Integer copilotDriverUserType;

    private String copilotDriverUserTypeName;

    private String followDriverUserName;

    private String followDriverMobilePhone;

    private Integer followDriverUserType;

    private String followDriverUserTypeName;

    private String orgName;

    private String shareUserName;

    private String shareMobilePhone;

    /**
     * 定位服务:0-无；1-G7；2-app定位; 3-易流
     */
    private Integer locationServ;

    private Long belongDriverUserId;

    private Long belongDriverUserName;

    private String belongDriverMobile;
    private boolean modify;

    private String failure;

    private Long tenantVehicleCostRelId;
    private Long tenantVehicleCertRelId;
}
