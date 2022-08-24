package com.youming.youche.record.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class VehicleInfoAppDto implements Serializable {

    private static final long serialVersionUID = -6322774735953419774L;

    private String plateNumber; // 车牌号码
    private String relId; // rel_id
    private Integer authState; // 自有车车队审核状态
    private String auditContent; // 自有车车队审核备注
    private String isAuth;
    private Integer sysAuthState; // 平台审核状态
    private String sysAuditContent; // 平台审核备注
    private Integer sysIsAuth;
    private String driverUserId; // 司机ID
    private String copilotDriverId; // 副驾驶ID
    private String vehicleValidityTime; // 行驶证有效期
    private String followDriverId; // 随车司机ID
    private String operateValidityTime; // 运营证有效期
    private Integer licenceType; // 牌照类型
    private String vehicleLength; // 车长(车型)
    private Integer vehicleStatus; // 类型(车型)
    private String lightGoodsSquare; // 容积（方）
    private String vehicleLoad; // 载重(吨)
    private String vinNo; // 车架号
    private String engineNo; // 发动机号
    private String drivingLicenseOwner; // 行驶证上所有人
    private Long vehiclePicture; // 车辆图片id
    private String vehiclePicUrl; // 车辆图片url
    private String drivingLicense; // 行驶证正本id
    private String drivingLicenseUrl; // 行驶证正本url
    private Long adriverLicenseCopy; // 行驶证副本id
    private String adriverLicenseCopyUrl; // 行驶证副本url
    private String drivingLicenseSn; // 行驶证号
    private String operCerti; // 运营证号
    private Long operCertiId; // 运营证号
    private String operCertiUrl; // 运营证号
    private String brandModel; // 车辆品牌
    private String billReceiverMobile; // 运营证号
    private String billReceiverName; // 运营证号
    private String billReceiverUserId; // 车辆品牌
    private String specialOperCertFileId; // 特殊营运证
    private String specialOperCertFileUrl; // 特殊营运证URL
    private Long vehicleCode; // 车辆主表id

    /**
     * 查询心愿线路
     */
    private List<VehicleObjectLineDto> vehicleOjbectLineArray;
    private Integer vehicleClass; // 车联类型
    private String vehicleClassName; // 车辆类型名称
    private String licenceTypeName; // 许可证名称
    private String vehicleStatusName; // 车结构类型
    private String vehicleLengthName; // 车长
    private String vehicleLoadName; // 车辆装载名称
    private String drivingLicenseFullUrl; // 驾驶执照url
    private String adriverLicenseCopyFullUrl; // 驾驶执照副本
    private String operCertiFullUrl; // 运营证
    private String specialOperCertFileFullUrl; // 特殊运营证
    private String vehiclePicFullUrl; //车辆副本
    private String authStateName; // 审核名称

}
