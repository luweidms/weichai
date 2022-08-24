package com.youming.youche.record.dto;

import com.youming.youche.record.domain.vehicle.VehicleLineRel;
import com.youming.youche.record.vo.VehicleObjectLineVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class VehicleInfoDto implements Serializable {

    private Long tenantId;


    private String tenantName;

    private Integer isUseCarOilCost;

    private Integer vehicleClass;

    private Long relId;

    private Integer isAuth;

    /**
     * 认证状态：1-未认证 2-已认证 3-认证失败
     */
    private Integer authState;


    /**
     * 联系人姓名
     */
    private String linkman;
    /**
     * 联系人电话
     */
    private String linkPhone;

    /**
     * 车辆编号
     */
    private Long vehicleCode;
    /**
     * 管理员--用户信息id
     */
    private Long adminUser;
    /**
     * 司机姓名
     */
    private String driverName;
    /**
     * 司机手机
     */
    private String driverPhone;

    private Boolean otherOwnCar;

    private Integer isOwnCar;

    private String linkAdminUser;

    private Long belongDriverUserId;

    private String belongDriverUserName;

    private String belongDriverMobile;

    private String vehicleClassName;

    private String licenceTypeName;

    private String vehicleStatusName;

    private String vehicleLengthName;

    private String authStateName;

    private String driverUserName;

    private  String driverMobilePhone;

    /**
     * 车长
     */
    private Long vehicleLength;

    /**
     * 需求车辆类型(对应静态数据的 code_type= VEHICLE_STATUS)
     */
    private Integer vehicleStatus;
    /**
     * 行驶证上所有人
     */
    private String drivingLicenseOwner;
    /**
     * 车辆租赁协议url
     */
    private String rentAgreementUrl;
    /**
     * 行驶证
     */
    private Long drivingLicense;
    private String drivingLicenseUrl;
    /**
     * 行驶证副本(图片保存编号)
     */
    private Long adriverLicenseCopy;
    private String adriverLicenseCopyUrl;
    /**
     * 行驶证编号
     */
    private String drivingLicenseSn;

    /**车辆品牌*/
    private String brandModel;
    /**车辆型号*/
    private String vehicleModel;

    /**
     * etc卡号
     */
    private String etcCardNumber;

    /**
     * 设备编码
     */
    private String equipmentCode;

    /**
     * 运营证图片ID(运输证)
     */
    private Long operCertiId;

    /**
     * 特殊运营证文件编号
     */
    private Long specialOperCertFileId;
    private String specialOperCertFileUrl;
    /**
     * 车辆图片
     */
    private Long vehiclePicture;
    private String vehiclePicUrl;
    /**
     * 发动机号
     */
    private String engineNo;

    /**
     * 车辆租赁协议
     */
    private Long rentAgreementId;
    /**
     * 运营证
     */
    private String operCerti;
    private String operCertiUrl;
    /**
     * 车架
     */
    private String vinNo;
    /**
     * 车辆载重
     */
    private String vehicleLoad;

    /**
     * 轻货方数
     */
    private String lightGoodsSquare;

    private  Integer driverCarUserType;

    private String driverCarUserTypeName;

    private String loadEmptyOilCost;

    private String loadFullOilCost;

    private String copilotDriverUserName;

    private String copilotDriverMobilePhone;

    private Integer copilotDriverUserType;

    private String copilotDriverUserTypeName;
    /**
     * 营运证有效期
     */
    private String operateValidityTime;
    /**
     * 牌照类型(1:整车，2：拖头)
     */
    private Integer licenceType;

    private Long followDriverId;

    private String followDriverUserName;

    private String followDriverMobilePhone;

    private Integer followDriverUserType;

    private String followDriverUserTypeName;

    private Long orgId;

    private String orgName;

    private Long userId;

    private String userName;

    private Integer shareFlg;

    private String shareFlgName;

    private Long linkUserId;

    private String shareUserName;

    /**
     * 账单接收人手机号
     */
    private String billReceiverMobile;
    /**
     * 账单接收人用户编号
     */
    private Long billReceiverUserId;

    /**
     * 行驶证有效期
     */
    private String vehicleValidityTime;
    /**
     * 副驾驶ID
     */
    private Long copilotDriverId;

    private String driverUserId;


    /**
     * 挂靠合同文件图片
     */
    private String attachContractFilePic;
    /**
     * 招商合同文件图片
     */
    private String investContractFilePic;

    private String billReceiverName;



    private String shareMobilePhone;

    private String tenantLinkMan;

    private String tenantLinkPhone;
    /**
     * 心愿路线
     */
    private List<VehicleObjectLineVo> vehicleOjbectLineArray;
    /**
     * 查询绑定线路
     */
    private List<VehicleLineRel> vehiclelineRels;

    private String plateNumber;

    private TenantVehicleRelInfoDto tenantVehicleRelInfoDto;




}
