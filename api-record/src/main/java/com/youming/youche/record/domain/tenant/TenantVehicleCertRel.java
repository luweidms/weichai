package com.youming.youche.record.domain.tenant;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * <p>
 *
 * </p>
 *
 * @author Terry
 * @since 2021-12-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TenantVehicleCertRel extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 关系ID
     */
    private Long relId;

    /**
     * 车辆编号
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long vehicleCode;

    /**
     * 租户id
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long tenantId;

    /**
     * 司机编号，冗余
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long userId;

    /**
     * 车牌号码，冗余
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String plateNumber;



    /**
     * 操作员id
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long opId;

    /**
     * 上次保养时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate prevMaintainTime;

    /**
     * 保险时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate insuranceTime;

    /**
     * 季审时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate seasonalVeriTime;

    /**
     * 年审时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate annualVeriTime;

    /**
     * 保险单号
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String insuranceCode;

    /**
     * 保养周期（公里）
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long maintainDis;

    /**
     * 保养预警公里
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long maintainWarnDis;

    /**
     * 登记日期
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate registrationTime;

    /**
     * 登记证号
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String registrationNumble;

    /**
     * 证照信息备注
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String remarkForVailidity;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer state;

    /**
     * 年审结束时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate annualVeriTimeEnd;

    /**
     * 季审结束时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate seasonalVeriTimeEnd;

    /**
     * 交强险结束时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate insuranceTimeEnd;

    /**
     * 商业险开始时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate busiInsuranceTime;

    /**
     * 商业险结束时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate busiInsuranceTimeEnd;

    /**
     * 商业险单号
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String busiInsuranceCode;

    /**
     * 其他险开始时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate otherInsuranceTime;

    /**
     * 其他险结束时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate otherInsuranceTimeEnd;

    /**
     * 其他险险单号
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String otherInsuranceCode;

    /**
     * 行驶证有效期开始时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate vehicleValidityTimeBegin;

    /**
     * 营运证有效期开始时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate operateValidityTimeBegin;

    /**
     * 行驶证有效期结束时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate vehicleValidityTime;

    /**
     * 营运证有效期结束时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate operateValidityTime;


}
