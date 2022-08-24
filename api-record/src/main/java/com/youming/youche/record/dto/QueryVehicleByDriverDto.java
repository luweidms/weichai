package com.youming.youche.record.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class QueryVehicleByDriverDto implements Serializable {

    private static final long serialVersionUID = -8394432661409587150L;

    private Long vehicleCode; // 车辆编号
    private String plateNumber; // 车牌号
    private String vehicleLength; // 车长
    private Long vehicleStatus; // 货箱结构
    private String vehicleLoad; // 车辆载重
    private String lightGoodsSquare; // 货物
    private String vehiclePicUrl;// 车辆照片
    private Integer licenceType; // 许可证
    private Integer authState; // 自有车车队审核状态
    private String auditContent; // 自有车车队审核备注
    private String isAuth; // 审核
    private Integer sysIsAuth;
    private Integer sysAuthState; // 平台审核状态
    private String sysAuditContent; // 平台审核备注
    private String relId;
    private String cooperationCount; // 合作次数
    private Integer shareFlg; // 是否共享
    private String vehicleClass; // 车辆类型

    private String authStateName; // 审核状态名称
    private String vehiclePicFullUrl; // 图片url
    private String vehicleStatusName; // 车结构
    private String vehicleLengthName; // 车长
    private String licenceTypeName; // 许可证类型

}
