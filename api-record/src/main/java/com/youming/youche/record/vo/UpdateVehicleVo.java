package com.youming.youche.record.vo;

import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UpdateVehicleVo implements Serializable {

    private static final long serialVersionUID = -8290669757432029751L;

    private Long vehicleCode;
    private String plateNumber; // 车牌号
    private Long driverUserId; // 司机id
    private Integer carUserType;
    private String vehiclePicture; // 车辆图片
    private String drivingLicense; // 行驶证正本
    private String adriverLicenseCopy; // 行驶证副本
    private String vehiclePicUrl;
    private String operCertiId; // 运输证
    private String operCertiUrl;
    private String drivingLicenseUrl;
    private String adriverLicenseCopyUrl;
    private String specialOperCertFileId; // 特殊运营证
    private String specialOperCertFileUrl;
    private Integer licenceType; // 牌照类型
    private Integer vehicleStatus; // 车辆类型
    private String vehicleLength; // 车长
    private Integer vehicleLoad; // 可载重
    private String lightGoodsSquare; // 容积
    private String vinNo; // 车架号
    private String engineNo; // 发动机号
    private String drivingLicenseSn;
    private String operCerti;
    private String brandModel; // 车辆品牌
    private String drivingLicenseOwner;//行驶证上所有人
    //账单接收人信息
    private Long billReceiverUserId;
    private String billReceiverMobile;
    private String billReceiverName;

    private List<VehicleObjectLineVo> lineData; //心愿线路
}
