package com.youming.youche.record.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 车辆年审导入错误表
 *
 * @author hzx
 * @date 2022/2/19 18:24
 */
@Data
public class VehicleAnnualReviewDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 申请单号
     */
    private String requestNo;

    /**
     * 车辆id
     */
    private Long vehicleId;

    /**
     * 车牌号码
     */
    private String vehicleCode;

    /**
     * 年审类型（1、行驶证年审，2、车辆年审）
     */
    private String annualreviewType;
    private String annualreviewTypeName;
    /**
     * 车辆的年审日期
     */
    private String annualreviewData;

    /**
     * 车辆年审费用
     */
    private String annualreviewCost;

    /**
     * 车辆年审的有效期截止日期
     */
    private String effectiveDate;

    /**
     * 代办年审的服务商
     */
    private String serviceProvider;

    /**
     * 拍照类型(1:整车，2：拖头)
     */
    private String licenceType;
    private String licenceTypeName;

    /**
     * 车辆归属部门
     */
    private String vehicleAscriptionDep;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    private Long tenantId;

    /**
     * (导入失败原因)
     */
    private String reasonFailure;

    public String getAnnualreviewTypeName() {
        if ("1".equals(annualreviewType)) {
            annualreviewTypeName = "行驶证年审";
        } else {
            annualreviewTypeName = "车辆年审";
        }
        return annualreviewTypeName;
    }

    public String getLicenceTypeName() {
        if ("1".equals(licenceType)) {
            licenceTypeName = "整车";
        } else {
            licenceTypeName = "拖头";
        }
        return licenceTypeName;
    }

}
