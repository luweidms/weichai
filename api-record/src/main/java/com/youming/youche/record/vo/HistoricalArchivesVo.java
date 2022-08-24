package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Date:2021/12/31
 */
@Data
public class HistoricalArchivesVo implements Serializable {

    private Long hisId;

    private Long vehicleCode;

    private String plateNumber;

    private Integer licenceType;

    private String licenceTypeName;

    private String vehicleLength;

    private String vehicleLengthName;

    private Integer vehicleStatus;

    private String vehicleStatusName;

    private Integer vehicleClass;

    private String vehicleClassName;

    private Long relId;

    private String linkman;

    private String mobilePhone;

    private Long tenantId;

    private String tenantName;

    private String linkPhone;

    private String vehicleInfo;

    private  String driverPhone;
}
