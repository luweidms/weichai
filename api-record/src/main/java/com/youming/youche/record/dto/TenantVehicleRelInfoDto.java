package com.youming.youche.record.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TenantVehicleRelInfoDto implements Serializable {
    private Long price;
    /**
     * 贷款利息
     */
    private Long loanInterest;

    /**
     * 还款期数
     */
    private Integer interestPeriods;

    /**
     * 已还款期数
     */
    private Integer payInterestPeriods;

    /**
     * 购买时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;
    /**
     * 折旧月数
     */
    private Integer depreciatedMonth;
    /**
     * 代收保险
     */
    private Long collectionInsurance;

    /**
     * 审车费
     */
    private Long examVehicleFee;

    /**
     * 维护费
     */
    private Long maintainFee;

    /**
     * 维修费
     */
    private Long repairFee;

    /**
     * 管理费
     */
    private Long managementCost;

    /**
     * 轮胎费
     */
    private Long tyreFee;

    /**
     * 其他费
     */
    private Long otherFee;

    /**
     * 残值
     */
    private Long residual;

    /**
     * 租户车辆成本相关 ID
     */
    private Long tenantVehicleCostRelId;


    /**
     * 年审时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate annualVeriTime;
    /**
     * 季审时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate seasonalVeriTime;


    /**
     * 年审结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate annualVeriTimeEnd;

    /**
     * 季审结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate seasonalVeriTimeEnd;

    /**
     * 上次交强险时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate insuranceTime;

    /**
     * 交强险结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate insuranceTimeEnd;

    /**
     * 保险单号
     */
    private String insuranceCode;

    /**
     * 商业险开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate busiInsuranceTime;

    /**
     * 商业险结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate busiInsuranceTimeEnd;

    /**
     * 商业险单号
     */
    private String busiInsuranceCode;

    /**
     * 其他险开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate otherInsuranceTime;

    /**
     * 其他险结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate otherInsuranceTimeEnd;

    /**
     * 其他险单号
     */
    private String otherInsuranceCode;


    /**
     * 保养周期（公里）
     */
    private Long maintainDis;

    /**
     * 保养预警公里
     */
    private Long maintainWarnDis;

    /**
     * 上次保养时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate prevMaintainTime;

    /**
     * 登记日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate registrationTime;

    /**
     * 登记证号
     */
    private String registrationNumble;

    /**
     * 行驶证有效开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate vehicleValidityTimeBegin;

    /**
     * 行驶证有效期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate vehicleValidityTime;


    /**
     * 营运证有效期开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate operateValidityTimeBegin;

    /**
     * 营运证有效期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate operateValidityTime;
    /**
     * id
     */
    private Long tenantVehicleCertRelId;

}
