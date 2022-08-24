package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Date:2022/2/12
 */
@Data
public class VehicledentAccidentVo implements Serializable {

    private Long managementId;

    /**
     *事故id
     */
    private Long id;

    /**
     *用户id
     */
    private Long userId;

    /**
     *车牌号码
     */
    private String plateNumber;

    /**
     *牌照类型(1:整车，2：拖头)
     */
    private Integer licenceType;

    /**
     *牌照类型(1:整车，2：拖头)
     */
    private String licenceTypeName;

    /**
     *车型
     */
    private Integer vehicleStatus;

    /**
     *车型名称
     */
    private String vehicleStatusName;

    /**
     *归属部门
     */
    private String departmentName;

    /**
     *品牌名称
     */
    private String brandModel;

    /**
     *品牌型号
     */
    private String vehicleModel;

    /**
     * 车辆code
     */
    private String vehicleCode;

    /**
     *出险时间
     */
    private String accidentDate;

    /**
     *出险地点
     */
    private String accidentPlace;

    /**
     *事故原因
     */
    private String accidentCause;

    /**
     *事故类型（1直行事故、2追尾事故、3超车事故，4左转弯事故，5右转变事故，6窄道事故，7弯道事故，8坡道事故，9会车事故，10超车事故，11停车事故）
     */
    private Integer accidentType;

    /**
     *事故类型（1直行事故、2追尾事故、3超车事故，4左转弯事故，5右转变事故，6窄道事故，7弯道事故，8坡道事故，9会车事故，10超车事故，11停车事故）
     */
    private String accidentTypeName;

    /**
     *本车车损
     */
    private String vehicleDamage;

    /**
     *对方车损
     */
    private String otherDamage;

    /**
     *道路损款
     */
    private String roadLoss;

    /**
     *是否物损（1、是，0、否）
     */
    private Integer materialAmage;

    /**
     *是否物损（1、是，0、否）
     */
    private String materialAmageName;

    /**
     *是否伤人（1、是，0、否）
     */
    private Integer wounding;

    /**
     *是否伤人（1、是，0、否）
     */
    private String woundingName;

    /**
     *肇事司机
     */
    private String accidentDriver;

    /**
     *主驾
     */
    private String mainDriver;

    /**
     *保险公司
     */
    private String insuranceCompany;

    /**
     *被保险人
     */
    private String Insured;

    /**
     *事故报案日期
     */
    private String reportDate;

    /**
     *保险理赔金额
     */
    private String claimAmount;

    private Integer vehicleClass;

    private String vehicleClassName;
}
