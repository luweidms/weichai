package com.youming.youche.order.domain.tenant;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author hzx
 * @since 2022-03-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TenantVehicleCertRel extends BaseDomain {

    /**
     * 关系ID
     */
    private Long relId;

    /**
     * 车辆编号
     */
    private Long vehicleCode;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 司机编号，冗余
     */
    private Long userId;

    /**
     * 车牌号码，冗余
     */
    private String plateNumber;

    /**
     * 操作员id
     */
    private Long opId;

    /**
     * 上次保养时间
     */
    private LocalDate prevMaintainTime;

    /**
     * 保险时间
     */
    private LocalDate insuranceTime;

    /**
     * 季审时间
     */
    private LocalDate seasonalVeriTime;

    /**
     * 年审时间
     */
    private LocalDate annualVeriTime;

    /**
     * 保险单号
     */
    private String insuranceCode;

    /**
     * 保养周期（公里）
     */
    private Integer maintainDis;

    /**
     * 保养预警公里
     */
    private Integer maintainWarnDis;

    /**
     * 登记日期
     */
    private LocalDate registrationTime;

    /**
     * 登记证号
     */
    private String registrationNumble;

    /**
     * 证照信息备注
     */
    private String remarkForVailidity;

    private Integer state;

    /**
     * 年审结束时间
     */
    private LocalDate annualVeriTimeEnd;

    /**
     * 季审结束时间
     */
    private LocalDate seasonallVeriTimeEnd;

    /**
     * 交强险结束时间
     */
    private LocalDate insuranceTimeEnd;

    /**
     * 商业险开始时间
     */
    private LocalDate busiInsuranceTime;

    /**
     * 商业险结束时间
     */
    private LocalDate busiInsuranceTimeEnd;

    /**
     * 商业险单号
     */
    private String busiInsuranceCode;

    /**
     * 其他险开始时间
     */
    private LocalDate otherInsuranceTime;

    /**
     * 其他险结束时间
     */
    private LocalDate otherInsuranceTimeEnd;

    /**
     * 其他险险单号
     */
    private String otherInsuranceCode;

    /**
     * 行驶证有效期开始时间
     */
    private LocalDate vehicleValidityTimeBegin;

    /**
     * 营运证有效期开始时间
     */
    private LocalDate operateValidityTimeBegin;

    /**
     * 行驶证有效期结束时间
     */
    private LocalDate vehicleValidityTime;

    /**
     * 营运证有效期结束时间
     */
    private LocalDate operateValidityTime;

    private LocalDateTime createDate;

    private LocalDate seasonalVeriTimeEnd;


}
