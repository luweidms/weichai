package com.youming.youche.record.domain.trailer;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 车队与挂车关系表
 * </p>
 *
 * @author Terry
 * @since 2022-01-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TenantTrailerRel extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 挂车编号
     */
    private Long trailerId;

    /**
     * 挂车牌
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String trailerNumber;

    /**
     * 操作时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String opTime;

    /**
     * 状态
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer state;

    /**
     * 操作人
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long opId;

    /**
     * 租户
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long tenantId;

    /**
     * 价格
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long price;

    /**
     * 轮胎费
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long tyreFee;

    /**
     * 保险费
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long insuranceFee;

    /**
     * 审车费
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long examVehicleFee;

    /**
     * 维护费
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
     * 折旧月
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer depreciatedMonth;

    /**
     * 上次保养时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String prevMaintainTime;

    /**
     * 上次交强险时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String insuranceTime;

    /**
     * 季审时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String seasonalVeriTime;

    /**
     * 年审时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String annualVeriTime;

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
     * 归属大区
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long attachedRootOrgId;

    /**
     * 所有权
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer trailerOwnerShip;

    /**
     * 归属部门(不分1，2级)
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long attachedRootOrgTwoId;

    /**
     *
     */
    @TableField(exist = false)
    private String attachedRootOrgTwoIdName;

    /**
     * 归属人Id
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long attachedManId;

    /**
     * 归属人
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String attachedMan;

    /**
     * 备注
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String remark;

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

    /**
     * 审核状态  0-待审核，1-审核通过，2-审核拒绝
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer isAutit;

    /**
     * 残值
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long residual;

    /**
     * 年审到期时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String annualVeriExpiredTime;

    /**
     * 季审到期时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String seasonalVeriExpiredTime;

    /**
     * 交强险到期时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate insuranceExpiredTime;

    /**
     * 上次商业险时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String businessInsuranceTime;

    /**
     * 商业险到期时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate businessInsuranceExpiredTime;

    /**
     * 商业险单号
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String businessInsuranceCode;

    /**
     * 上次其他险时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String otherInsuranceTime;

    /**
     * 其他险到期时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate otherInsuranceExpiredTime;

    /**
     * 其他险单号
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String otherInsuranceCode;


}
