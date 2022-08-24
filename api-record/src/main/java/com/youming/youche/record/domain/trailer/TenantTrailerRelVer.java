package com.youming.youche.record.domain.trailer;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
public class TenantTrailerRelVer extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 挂车关系id
     */
    private Long relId;

    /**
     * 挂车编号
     */
    private Long trailerId;
    /**
     * 挂车牌
     */
    private String trailerNumber;

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
    private Long typeFee;

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
    private Integer depreaciatedMonth;

    /**
     * 上次保养时间
     */
    private String prevMaintainTime;

    /**
     * 上次交强险时间
     */
    private String insuranceTime;

    /**
     * 季审时间
     */
    private String seasonalVerlTime;

    /**
     * 年审时间
     */
    private String annualVeriTime;

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
     *
     */
    @TableField(exist = false)
    private String trailerOwnerShipName;

    /**
     * 归属部门
     */
    private Long attachedRootOrgTwoId;
    /**
     * 当前租户下组织名称
     */
    @TableField(exist = false)
    private String attachedRootOrgTwoIdName;

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
     * 修改操作人
     */
    private Long UpdateOpId;

    /**
     * 是否审核   1为是 0 为否
     */
    private Integer isAutit;

    /**
     * 残值
     */
    private Long residual;

    /**
     * 年审到期时间
     */
    private String annualVeriExpiredTime;

    /**
     * 季审到期时间
     */
    private String seasonalVeriExpiredTime;

    /**
     * 交强险到期时间
     */
    private String insuranceExpiredTime;

    /**
     * 上次商业险时间
     */
    private String businessInsuranceTime;

    /**
     * 商业险到期时间
     */
    private String businessInsuranceExpiredTime;

    /**
     * 商业险单号
     */
    private String businessInsuranceCode;

    /**
     * 上次其他险时间
     */
    private String otherInsuranceTime;

    /**
     * 其他险到期时间
     */
    private String otherInsuranceExpiredTime;

    /**
     * 其他险单号
     */
    private String otherInsuranceCode;

    /**
     * 9、被移除的数据
     */
    private Integer deleteFlag;


}
