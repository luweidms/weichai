package com.youming.youche.record.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Date:2022/2/23
 */
@Data
public class OBMSVehicleDataVo implements Serializable {


    /**
     * 车辆
     */
    private Long vehicleCode;
    /**
     * 车牌
     */
    @NotBlank(message = "请填写车牌号码")
    private String plateNumber;

    /**
     * 车辆类型
     */
    @NotNull(message = "请选择车辆类型")
    private Integer licenceType;


    /**
     * 车长
     */
    private String vehicleLength;


    /**
     * 车辆类型
     */
    private Integer vehicleStatus;


    /**
     * 司机id
     */
    @Min(value = 0,message = "请选择司机")
    private Long driverUserId;

    /**
     * 车辆载重
     */
    private String vehicleLoad;
    /**
     * 车辆容积
     */
    private String lightGoodsSquare;
    /**
     * 车架号
     */
    @NotBlank(message = "请上传车架号")
    private String vinNo;
    /**
     * 运输证号
     */
    private String operCerti;
    /**
     * 发动机号
     */
    @NotBlank(message = "请填写发动机号")
    private String engineNo;
    private String drivingLicenseSn;
    /**
     * 所有人
     */
    @NotBlank(message = "请输入行驶证上的所有人")
    private String drivingLicenseOwner;
    /**
     * 司机姓名
     */
    private String driverUserName;
    /**
     * 司机手机号
     */
    private String driverUserPhone;

    /**
     * 被挂靠人
     */
    private String attachUserName;
    /**
     * 挂靠于
     */
    private String attachUserMobile;
    /***挂靠人id*/
    private Long attachUserId;
    @Deprecated
    private String billReceiverUserId;
    @Deprecated
    private String billReceiverName;
    /**
     * 心愿路线
     */
    private List<VehicleObjectLineVo> vehicleOjbectLineArray;

    /**
     * 车辆图片id
     */
    private Long vehiclePicture;
    /**
     * 车辆图片url
     */
    @NotBlank(message = "请上传车辆图片")
    private String vehiclePicUrl;
    /**
     * 行驶证正本id
     */
    private Long drivingLicense;
    /**
     * 行驶证正本url
     */
    @NotBlank(message = "请上传行驶证正本")
    private String drivingLicenseUrl;
    /**
     * 行驶证副本id
     */
    private Long adriverLicenseCopy;
    /**
     * 行驶证副本url
     */
    @NotBlank(message = "请上传行驶证副本")
    private String adriverLicenseCopyUrl;

    /**
     * 特殊营运证id
     */
    private String specialOperCertFileUrl;
    /**
     * 特殊营运证id
     */
    private Long specialOperCertFileId;


}
