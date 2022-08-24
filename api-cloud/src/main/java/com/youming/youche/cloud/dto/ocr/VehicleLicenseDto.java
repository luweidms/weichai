package com.youming.youche.cloud.dto.ocr;

import lombok.Data;

import java.io.Serializable;

@Data
public class VehicleLicenseDto implements Serializable {


    /**
     * 车牌号
     */
    private String number;

    /**
     *  车辆类型
     */
    private String vehicleType;

    /**
     * 驾驶员姓名
     */
    private String name;

    /**
     * 地址
     */
    private String address;

    /**
     * 使用性质
     */
    private String useCharacter;

    /**
     * 品牌
     */
    private String model;

    /**
     * 发动机号
     */
    private String engineNo;

    /**
     * 车辆识别号
     */
    private String vin;

    /**
     * 注册日期
     */
    private String registerDate;

    /**
     * 发证日期
     */
    private String issueDate;

    /**
     *
     */
    private String issuingAuthority;

    /**
     * 档案编号
     */
    private String fileNo;

    /**
     * 人数
     */
    private String approvedPassengers;

    /**
     * 总重量
     */
    private String grossMass;

    /**
     * 整备重量
     */
    private String unladenMass;
    /**
     * 核定承载人数
     */
    private String approvedLoad;
    /**
     * 尺寸
     */
    private String dimension;

    private String tractionMass;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 简约有效期
     */
    private String inspectionRecord;

    /**
     * 卡号
     */
    private String codeNumber;

    private Object textLocation;
}
