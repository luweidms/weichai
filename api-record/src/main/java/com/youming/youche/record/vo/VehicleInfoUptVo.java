package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * @Date:2022/1/13
 */
@Data
public class VehicleInfoUptVo implements Serializable {

    /**
     * 品牌型号
     */
    private String brandModel;


    /**
     * 用户ID/发布人用户编号(司机归属用户ID)
     */
    private Long userId;

    /**
     * 所有人
     */
    private String drivingLicenseOwner;

    private Long tenantVehicleCostRelId;

    private Long tenantVehicleCertRelId;

    private Long adriverLicenseCopy;

    /**
     * 车型类型名称
     */
    private String vehicleStatusName;

    /**
     * 车架
     */
    private String vinNo;

    /**
     * 车队负责人
     */
    private String linkMan;

    /**
     * 车长名称
     */
    private String vehicleLengthName;

    /**
     * 司机类型名称
     */
    private String driverCarUserTypeName;

    /**
     * 司机名称
     */
    private String driverUserName;

    private List<VehicleObjectLineVo> vehicleOjbectLineArray;



    /**
     * 认证名称
     */
    private String authStateName;

    /**
     * 发动机号
     */
    private String engineNo;

    /**
     * 认证状态
     */
    private Integer authState;

    /**
     * 车辆载重
     */
    private Integer vehicleLoad;

    /**
     * 账单接收人手机号
     */
    private String billReceiverMobile;
    private Long billReceiverUserId;/**
     * 账单接收人姓名
     */
    private String billReceiverName;

    /**
     * 购买时间
     */
    private String purchaseDate;

    /**
     * 年审开始时间
     */
    private String annualVeriTime;

    /**
     * 年审到期时间
     */
    private String annualVeriTimeEnd;

    /**
     * 季审开始时间
     */
    private String seasonalVeriTime;

    /**
     * 营运证有效开时间
     */

    private LocalDate seasonalVeriTimeEnd;

    /**
     * 营运证有效结束时间
     */
    private String operateValidityTimeBegin;

    /**
     * 季审到期时间
     */
    private String operateValidityTime;

    /**
     * 行驶证有效开始时间
     */
    private String vehicleValidityTimeBegin;

    /**
     * 行驶证有效结束时间
     */
    private String vehicleValidityTime;

    /**
     * 商业险开始时间
     */
    private String busiInsuranceTime;

    /**
     * 商业险结束时间
     */
    private String busiInsuranceTimeEnd;

    /**
     * 交强险开始时间
     */
    private String insuranceTime;

    /**
     * 交强险结束时间
     */
    private String insuranceTimeEnd;

    /**
     * 其他险开始时间
     */
    private String otherInsuranceTime;

    /**
     * 其他险结束时间
     */
    private String otherInsuranceTimeEnd;

    /**
     * 上次保养时间
     */
    private String prevMaintainTime;

    /**
     * 登记时间
     */
    private String registrationTime;


    /**
     * 司机类型
     */
    private Integer driverCarUserType;

    /**
     * 是否共享 0不 1是
     */
    private Integer shareFlg;

    private Long vehicleCode;

    /**
     * 牌照类型
     */
    private String licenceTypeName;

    private String tenantLinkMan;



    private boolean otherOwnCar;

    /**
     * 空载油耗
     */
    private Long loadEmptyOilCost;

    /**
     * 司机手机号
     */
    private String driverMobilePhone;
    /**
     * 车型类型
     */
    private Integer vehicleStatus;

    private String shareUserName;

    private String shareMobilePhone;

    /**
     * 联系人id
     */
    private Long linkUserId;

    /**
     * 副驾名称
     */
    private String copilotDriverUserName;

    /**
     * 随车司机名称
     */
    private String followDriverUserName;

    private String userName;

    private String linkPhone;

    private Integer isUseCarOilCost;

    private Integer isAuth;

    /**
     * 归属车队名称
     */
    private String tenantName;

    /**
     * 牌照类型
     */
    private Integer licenceType;

    /**
     * 车队联系电话
     */
    private String tenantLinkPhone;

    /**
     * 车辆种类
     */
    private Integer vehicleClass;

    private Long driverUserId;

    private Long relId;

    private String plateNumber;

    /**
     * 载重油耗
     */
    private Long loadFullOilCost;

    /**
     * 归属部门名称
     */
    private String orgName;

    private Integer isOwnCar;

    /**
     * 车辆种类名称
     */
    private String vehicleClassName;

    /**
     * 车长
     */
    private String vehicleLength;

    private String adminUser;

    private String drivingLicense;

    private String vehiclePicture;

    private String drivingLicenseSn;

    private Long tenantId;

    /**
     * 运输证号
     */
    private String operCerti;
    /**
     * 运输证号
     */
    private Long operCertiId;

    /**
     * 车辆容积
     */
    private Integer lightGoodsSquare;

    /**
     * 副驾类型
     */
    private Integer copilotDriverUserType;

    /**
     * 随车司机类型
     */
    private Integer followDriverUserType;

    private String vehiclePicUrl;

    private String drivingLicenseUrl;

    private String adriverLicenseCopyUrl;

    private Integer vehicleClassOld;

    private String driverUserIdOld;

    private String operCertiUrl;

    private String specialOperCertFileUrl;

    private Long specialOperCertFileId;

    private List<VehicleLineRelsVo> vehicleLineRels;

    /**
     * 车辆型号
     */
    private String vehicleModel;

    /**
     * 购车价格
     */
    private String price;    /**
     * 管理费
     */
    private String managementCost;

    /**
     * 残值
     */
    private String residual;

    /**
     * 贷款利息
     */
    private String loanInterest;

    /**
     * 已还期数
     */
    private Long payInterestPeriods;

    /**
     * 还款期数
     */
    private Long interestPeriods;

    /**
     * 折旧月数
     */
    private Long depreciatedMonth;

    /**
     * 保险费用
     */
    private String collectionInsurance;

    /**
     * 审车费用
     */
    private String examVehicleFee;

    /**
     * 保养费用
     */
    private String maintainFee;

    /**
     * 维修费用
     */
    private String repairFee;

    /**
     * 轮胎费用
     */
    private String tyreFee;

    /**
     * 其他费用
     */
    private String otherFee;

    /**
     * 商业险单号
     */
    private String busiInsuranceCode;

    /**
     * 交强险单号
     */
    private String insuranceCode;

    /**
     * 其他险单号
     */
    private String otherInsuranceCode;


    /**
     * 登记证号
     */
    private String registrationNumble;

    /**
     * 保养周期公里
     */
    private String maintainDis;

    /**
     * 保养周期公里
     */
    private String maintainWarnDis;

    /**
     * 部门id
     */
    private Long orgId;


/**副驾司机*/
    private Long copilotDriverId;

    private String copilotDriverName;

    private String copilotDriverPhone;

    private String copilotDriverTypeName;

    /**随车司机*/

    private Long followDriverId;

    private String followDriverName;

    private String followDriverPhone;

    private String followDriverTypeName;


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
    private Long investContractFilePic;
}
