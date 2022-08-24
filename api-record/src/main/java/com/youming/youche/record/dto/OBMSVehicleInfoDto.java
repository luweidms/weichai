package com.youming.youche.record.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.youming.youche.record.vo.VehicleObjectLineVo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
public class OBMSVehicleInfoDto implements Serializable {


    private static final long serialVersionUID = 686415190806179020L;
    /**
     * 车牌号码
     */
    private String plateNumber;
    /**平台审核状态 认证状态：1-未认证 2-已认证 3-认证失败*/
    private Integer authState;
    private String authStateName;
    /**平台审核备注  认证内容*/
    private String auditContent;
    /**是否显示审核按钮 0否 1是*/
    private Integer isAuth;
    /**司机ID  司机用户编号*/
    private Long driverUserId;
    private String driverUserName;
    private String driverUserPhone;
    /**副驾驶ID*/
    private Long copilotDriverId;
    /**行驶证有效期*/
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate vehicleValidityTime;
    /**随车司机ID*/
    private Long followDriverId;
    /**运营证有效期*/
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate operateValidityTime;
    /**牌照类型  牌照类型(1:整车，2：拖头)*/
    private Integer licenceType;
    private String licenceTypeName;
    /**车长(车型)*/
    private String vehicleLength;
    private String vehicleLengthName;
    /**类型(车型)*/
    private Integer vehicleStatus;
    private String vehicleStatusName;
    /**容积（方）*/
    private String lightGoodsSquare;
    /**载重(吨)*/
    private String vehicleLoad;
    /**车架号*/
    private String vinNo;
    /**运营证号*/
    private String operCerti;
    /**行驶证上所有人*/
    private String drivingLicenseOwner;
    /**发动机号*/
    private String engineNo;
    /**车辆租赁协议*/
    private Long rentAgreementId;
    /**车辆租赁协议url*/
    private String rentAgreementUrl;
    /**车辆图片id*/
    private Long vehiclePicture;
    /**车辆图片url*/
    private String vehiclePicUrl;
    /**行驶证正本id*/
    private String drivingLicense;
    /**行驶证正本url*/
    private String drivingLicenseUrl;
    /**行驶证副本id*/
    private Long adriverLicenseCopy;
    /**行驶证副本url*/
    private String adriverLicenseCopyUrl;
    /**运营证url*/
    private Long specialOperCertFileId;
    /**行驶证号*/
    private String drivingLicenseSn;
    /**车辆品牌*/
    private String brandModel;
    /**车辆主表id*/
    private Long vehicleCode;
    /**挂靠人手机号*/
    private String attachUserMobile;
    /**挂靠人名称*/
    private String attachUserName;
    /**挂靠人用户编号*/
    private Long attachUserId;
    /**租户编号*/
    private Long tenantId;
    /**是否共享*/
    private String shareFlg;
    private String shareFlgName;

    private List<VehicleObjectLineVo> vehicleOjbectLineArray;
}
