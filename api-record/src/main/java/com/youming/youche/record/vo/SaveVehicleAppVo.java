package com.youming.youche.record.vo;

import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class SaveVehicleAppVo implements Serializable {

    private static final long serialVersionUID = 2730576161926466497L;

    private String plateNumber; // 车牌号
    private Long driverUserId; // 司机id
    private Integer carUserType;
    private String vehiclePicture; // 车辆图片
    private String vehiclePicUrl;
    private String operCertiId; // 运输证
    private String operCertiUrl;
    private String drivingLicense; // 行驶证正本
    private String drivingLicenseUrl;
    private String adriverLicenseCopy; // 行驶证副本
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
    private String drivingLicenseSn; // 行驶证号
    private String operCerti; // 运输证号
    private String brandModel; // 车辆品牌
    private String drivingLicenseOwner; // 行驶证上所有人
    //添加账单接受人信息
    private Long billReceiverUserId;
    private String billReceiverMobile;
    private String billReceiverName;
    private Boolean autoAudit; // 0不自动审核 1自动审核通过

    private List<LineDataDto> lineData; //心愿线路


}
