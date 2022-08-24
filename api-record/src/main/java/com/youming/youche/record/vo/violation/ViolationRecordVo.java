package com.youming.youche.record.vo.violation;

import lombok.Data;

import java.io.Serializable;

/**
 * @Date:2022/1/28
 */
@Data
public class ViolationRecordVo implements Serializable {


    /**
     * 违章记录id
     */
    private Long  id;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 货箱架构
     */
    private Integer vehicleStatus;

    /**
     * 车型名称
     */
    private String vehicleStatusName;

    /**
     * 牌照类型 1整车 2拖头
     */
    private Integer licenceType;

    /**
     * 牌照类型名称
     */
    private String licenceTypeName;

    /**
     * 车长
     */
    private Integer vehicleLength;

    /**
     * 违章时间
     */
    private String violationTime;

    /**
     * 处理时间
     */
    private String renfaTime;

    /**
     * 违章类型 1、电子眼；2、现场单；3、已处理未缴费
     */
    private Integer categoryId;

    /**
     * 违章名称
     */
    private String categoryName;

    /**
     * 罚款金额
     */
    private Long violationFine;

    /**
     * 违章扣分
     */
    private Integer violationPoint;

    /**
     * 违章城市名
     */
    private String locationName;

    /**
     * 违章地址
     */
    private String violationAddress;

    /**
     * 违章原因
     */
    private String violationReason;

    /**
     * 处理状态 0、未处理；1、处理中；2、已完成
     */
    private Integer recordState;

    private String recordStateName;

    private Integer vehicleClass;

    private String vehicleClassName;
}
