package com.youming.youche.record.domain.tenant;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
public class TenantVehicleCostRel extends BaseDomain {

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
     * 租金
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long vehicleRent;

    /**
     * 管理费
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long managementCost;

    /**
     * 代收保险
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long collectionInsurance;

    /**
     * 车贷
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long carLoan;

    /**
     * 保险费
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long insuranceFee;

    /**
     * 轮胎费
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long tyreFee;

    /**
     * 审车费
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long examVehicleFee;

    /**
     * 保养费
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long maintainFee;

    /**
     * 维修费
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long repairFee;

    /**
     * 其他费
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long otherFee;

    /**
     * 购买时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate purchaseDate;

    /**
     * 折旧月数
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer depreciatedMonth;

    /**
     * 价格
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long price;

    /**
     * 贷款利息
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long loanInterest;

    /**
     * 还款期数
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer interestPeriods;

    /**
     * 已还款期数
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer payInterestPeriods;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer state;

    /**
     * 残值
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long residual;


}
