package com.youming.youche.record.vo;

import com.github.pagehelper.util.StringUtil;
import com.youming.youche.record.domain.base.BeidouPaymentRecord;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Date:2021/12/21
 */
@Data
public class TenantVehicleRelVo implements Serializable {

    private String createTime;

    /**
     * 车辆编号
     */
    private Long vehicleCode;

    /**
     * 车牌号码
     */
    private String plateNumber;

    /**
     * 牌照类型(1:整车，2：拖头)
     */
    private Integer licenceType;

    /**
     * 车长
     */
    private String vehicleLength;

    /**
     * 货箱结构
     */
    private Integer vehicleStatus;

    /**
     * 定位服务:0-无；1-G7；2-app定位; 3-易流
     */
    private Integer locationServ;

    /**
     * 维保里程
     */
    private Integer maintenanceDis;

    /**
     * 最后维保时间
     */
    private Date lastMaintenanceDate;

    /**
     * 车辆类别
     */
    private Integer vehicleClass;

    /**
     * 关系ID
     */
    private Long relId;

    /**
     * 关系ID
     */
    private Long eid;

    /**
     * 是否共享出去
     */
    private Integer shareFlg;

    /**
     * 认证状态：1-未认证 2-已认证 3-认证失败
     */
    private Integer authState;

    /**
     * 是否显示审核按钮(0 显示  1不显示)
     */
    private Integer isAuth;

    /**
     * 审核内容
     */
    private String auditContent;

    /**
     * 司机id
     */
    private Long driverUserId;

    /**
     * 司机
     */
    private String linkman;

    /**
     * 司机手机
     */
    private String mobilePhone;

    /**
     * 归属车队
     */
    private Long tenantId;

    /**
     * 租户名称(车队全称)
     */
    private String tenantName;

    /**
     * 车队手机
     */
    private String linkPhone;

    /**
     * 是否更换车牌
     */
    private Boolean isUpdatePlateNumber;

    /**
     * 定位时间
     */
    private String beiDouSerivceDate;

    /**
     * ETC卡号
     */
    private String etcCardNo;

    private List<BeidouPaymentRecord> beidouPaymentRecordList;

    private Integer beiDouMulti;

    /**
     * 服务商审核状态 1.未认证 2.已认证 3.认证失败
     */
    private String authStateName;

    /**
     * 车辆类别 1自有公司车 2招商挂靠车 3临时外调车 4外来挂靠车 5外调合同车
     */
    private String vehicleClassName;

    /**
     * 牌照类型 1整车 2拖头
     */
    private String licenceTypeName;

    /**
     * 是否接单 1不接单，2接单'
     */
    private Integer isWorking;

    /**
     * 定位类型名称
     */
    private String locationServName;

    // 车辆类型 类型
    private String vehicleStatusName;
    // 车辆类型 车长
    private String vehicleLengthName;
    //0 闲置 1禁用
    private Short idle;

    // 车辆类型
    private String vehicleTypeName;

    public String getVehicleTypeName() {
        vehicleTypeName = "";
        if (StringUtil.isNotEmpty(vehicleStatusName) || StringUtil.isNotEmpty(vehicleLengthName)) {
            vehicleTypeName = vehicleStatusName + "/" + vehicleLengthName;
        }
        return vehicleTypeName;
    }

    private String shareFlgName;

    /**
     * 合作车队
     */
    private String cooperationCount;

    private String vehicleInfo;

    public String getShareFlgName() {
        if (this.shareFlg == 0) {
            this.setShareFlgName("否");
        } else {
            this.setShareFlgName("是");
        }
        return shareFlgName;
    }

    public String getLicenceTypeName() {
        if (this.licenceType != null && this.licenceType == 1) {
            this.setLicenceTypeName("整车");
        } else {
            this.setLicenceTypeName("拖头");
        }
        return licenceTypeName;
    }

    //定位服务:0-无；1-G7；2-app定位; 3-易流
    public String getLocationServName() {
        if (this.locationServ == null || this.locationServ == 0) {
            this.setLocationServName("无");
        } else if (this.locationServ == 1) {
            this.setLocationServName("G7");
        } else if (this.locationServ == 2) {
            this.setLocationServName("app定位");
        } else if (this.locationServ == 3) {
            this.setLocationServName("易流");
        }
        return locationServName;
    }
}
