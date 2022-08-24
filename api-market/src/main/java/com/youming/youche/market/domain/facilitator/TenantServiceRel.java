package com.youming.youche.market.domain.facilitator;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Table;

/**
 * <p>
 * 服务商与租户关系
 * </p>
 *
 * @author Terry
 * @since 2022-01-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Table(name = "tenant_service_rel")
public class TenantServiceRel extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 租户
     */
    private Long tenantId;

    /**
     * 状态（1.有效、2.无效）
     */
    private Integer state;

    /**
     * 审核原因
     */
    private String authReason;

    /**
     * 审核人
     */
    private Long authManId;

    /**
     * 审核状态（1.待审核、2.审核通过、3.审核不通过）
     */
    private Integer authState;

    /**
     * 是否开票（1.是、2.否）
     */
    private Integer isBill;

    /**
     * 账期
     */
    private Integer paymentDays;


    /**
     * 操作人
     */
    private Long opId;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 审核时间
     */
    private String authDate;

    /**
     * 使用对象,用于控制实体油卡使用的车辆类型,对应车辆类型value值,null是全部车辆
     */
    private String operatorEntity;

    /**
     * 服务商ID
     */
    private Long serviceUserId;



    /**
     * 0 否 1是
     */
    private Integer isAuth;

    /**
     * 审核人名称
     */
    private String authManName;

    /**
     * 结算方式，1账期，2月结
     */
    private Integer balanceType;

    /**
     * 账期结算月份
     */
    private Integer paymentMonth;

    /**
     * 邀请状态，1待处理，2已通过，3被驳回
     */
    private Integer invitationState;

    /**
     * 授信金额
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long quotaAmt;

    /**
     * 已使用授信金额
     */
    private Long useQuotaAmt;





}
