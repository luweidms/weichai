package com.youming.youche.record.domain.vehicle;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @description 车辆年审
 * @date 2022/1/14 16:32
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class VehicleAnnualReview extends BaseDomain {

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
     * 部门id
     */
    private Long orgId;

    /**
     * 操作人id
     */
    private Long userDataId;

    /**
     * 车牌号码
     */
    private String vehicleCode;

    /**
     * 年审类型（1、行驶证年审，2、运营证年审）
     */
    private String annualreviewType;
    @TableField(exist = false)
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
    @TableField(exist = false)
    private String licenceType;
    @TableField(exist = false)
    private String licenceTypeName;

    /**
     * 车辆归属部门
     */
    @TableField(exist = false)
    private String vehicleAscriptionDep;

    /**
     * 开始时间
     */
    @TableField(exist = false)
    private String startTime;

    /**
     * 结束时间
     */
    @TableField(exist = false)
    private String endTime;

    /**
     * 车队id
     */
    private Long tenantId;

    public String getAnnualreviewTypeName() {
        if ("1".equals(annualreviewType)) {
            annualreviewTypeName = "行驶证年审";
        } else if ("2".equals(annualreviewType)) {
            annualreviewTypeName = "营运证年审";
        }
        return annualreviewTypeName;
    }

    public String getLicenceTypeName() {
        if ("1".equals(licenceType)) {
            licenceTypeName = "整车";
        } else if ("2".equals(licenceType)) {
            licenceTypeName = "拖头";
        }
        return licenceTypeName;
    }

}
