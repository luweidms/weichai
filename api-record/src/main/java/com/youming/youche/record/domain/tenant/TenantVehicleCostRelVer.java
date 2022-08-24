package com.youming.youche.record.domain.tenant;

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
public class TenantVehicleCostRelVer extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     *  TenantVehicleCostRel的主键id
     */
    private Long hisId;

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
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 操作员id
     */
    private Long opId;

    /**
     * 租金
     */
    private Long vehicleRent;

    /**
     * 管理费
     */
    private Long managementCost;

    /**
     * 代收保险
     */
    private Long collectionInsurance;

    /**
     * 车贷
     */
    private Long carLoan;

    /**
     * 保险费
     */
    private Long insuranceFee;

    /**
     * 轮胎费
     */
    private Long tyreFee;

    /**
     * 审车费
     */
    private Long examVehicleFee;

    /**
     * 保养费
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
     * 折旧月数
     */
    private Integer depreciatedMonth;

    /**
     * 价格
     */
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

    private Integer state;

    private LocalDateTime updateDate;

    private Long updateOpId;

    /**
     * 0不可用 1可用 9被移除
     */
    private Integer verState;

    /**
     * 残值
     */
    private Long residual;

    /**
     * 是否审批成功 1-是,3-自动审核通过
     */
    private Integer isAuthSucc;


}
