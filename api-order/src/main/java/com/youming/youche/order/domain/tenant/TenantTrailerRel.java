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
public class TenantTrailerRel extends BaseDomain {

    private Long trailerId;

    /**
     * 挂车牌
     */
    private String trailerNumber;

    /**
     * 操作时间
     */
    private LocalDateTime opTime;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 操作人
     */
    private Long opId;

    /**
     * 租户
     */
    private Long tenantId;

    /**
     * 价格
     */
    private Long price;

    /**
     * 轮胎费
     */
    private Long tyreFee;

    /**
     * 保险费
     */
    private Long insuranceFee;

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
     * 其他费
     */
    private Long otherFee;

    /**
     * 购买时间
     */
    private LocalDate purchaseDate;

    /**
     * 折旧月
     */
    private Integer depreciatedMonth;

    /**
     * 上次保养时间
     */
    private LocalDate prevMaintainTime;

    /**
     * 上次交强险时间
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
     * 归属大区
     */
    private Long attachedRootOrgId;

    /**
     * 所有权
     */
    private Integer trailerOwnerShip;

    /**
     * 归属部门(不分1，2级)
     */
    private Long attachedRootOrgTwoId;

    /**
     * 归属人Id
     */
    private Long attachedManId;

    /**
     * 归属人
     */
    private String attachedMan;

    /**
     * 备注
     */
    private String remark;

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
     * 审核状态  0-待审核，1-审核通过，2-审核拒绝
     */
    private Integer isAutit;

    /**
     * 残值
     */
    private Long residual;

    /**
     * 年审到期时间
     */
    private LocalDate annualVeriExpiredTime;

    /**
     * 季审到期时间
     */
    private LocalDate seasonalVeriExpiredTime;

    /**
     * 交强险到期时间
     */
    private LocalDate insuranceExpiredTime;

    /**
     * 上次商业险时间
     */
    private LocalDate businessInsuranceTime;

    /**
     * 商业险到期时间
     */
    private LocalDate businessInsuranceExpiredTime;

    /**
     * 商业险单号
     */
    private String businessInsuranceCode;

    /**
     * 上次其他险时间
     */
    private LocalDate otherInsuranceTime;

    /**
     * 其他险到期时间
     */
    private LocalDate otherInsuranceExpiredTime;

    /**
     * 其他险单号
     */
    private String otherInsuranceCode;

    private LocalDateTime createDate;


}
