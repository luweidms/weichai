package com.youming.youche.market.domain.facilitator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 服务商与租户关系
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TenantServiceRelVer  extends BaseDomain {

    private static final long serialVersionUID = 1L;




    /**
     * 关系主键
     */
    @TableField("REL_ID")
    private Long relId;

    /**
     * 租户
     */
    @TableField("TENANT_ID")
    private Long tenantId;

    /**
     * 状态（1.有效、2.无效）
     */
    @TableField("STATE")
    private Integer state;

    @TableField(exist = false)
    private String stateName;

    /**
     * 审核原因
     */
    @TableField("AUTH_REASON")
    private String authReason;

    /**
     * 审核人
     */
    @TableField("AUTH_MAN_ID")
    private Long authManId;

    /**
     * 审核状态（1.待审核、2.审核通过、3.审核不通过）
     */
    @TableField("AUTH_STATE")
    private Integer authState;

    /**
     * 是否开票（1.是、2.否）
     */
    @TableField("IS_BILL")
    private Integer isBill;

    /**
     * 账期
     */
    @TableField("PAYMENT_DAYS")
    private Integer paymentDays;



    /**
     * 操作人
     */
    @TableField("OP_ID")
    private Long opId;

    /**
     * 创建人ID
     */
    @TableField("CREATOR_ID")
    private Long creatorId;

    /**
     * 审核时间
     */
    @TableField("AUTH_DATE")
    private String authDate;

    /**
     * 使用对象,用于控制实体油卡使用的车辆类型,对应车辆类型value值,null是全部车辆
     */
    @TableField("OPERATOR_ENTITY")
    private String operatorEntity;

    /**
     * 服务商ID
     */
    @TableField("SERVICE_USER_ID")
    private Long serviceUserId;

    /**
     * 操作时间
     */
    @TableField("OP_DATE")
    private String opDate;

    /**
     * 操作员
     */
    @TableField("UPDATE_OP_ID")
    private Long updateOpId;


    /**
     * 是否删除（0：否，1：是）
     */
    @TableField("IS_DEL")
    private Integer isDel;

    /**
     * 结算方式，1账期，2月结
     */
    @TableField("BALANCE_TYPE")
    private Integer balanceType;

    /**
     * 账期结算月份
     */
    @TableField("PAYMENT_MONTH")
    private Integer paymentMonth;

    /**
     * 授信金额
     */
    @TableField("QUOTA_AMT")
    private Long quotaAmt;

    /**
     * 授信金额
     */
    @TableField("QUOTA_AMT_REL")
    private Long quotaAmtRel;

    /**
     * 服务商申请合作主键
     */
    @TableField("INVITATION_ID")
    private Long invitationId;

    /**
     * 结算类型
     */
    @TableField(exist = false)
    private String balanceTypeName;


}
