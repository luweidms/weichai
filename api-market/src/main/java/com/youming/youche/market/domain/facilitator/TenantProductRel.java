package com.youming.youche.market.domain.facilitator;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 租户与站点关系表
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TenantProductRel extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 站点ID
     */
    private Long productId;

    /**
     * 审核状态（1.未审核、2.审核通过、3.审核未通过）
     */
    private Integer authState;

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
     * 浮动价结算
     */
    @TableField( updateStrategy = FieldStrategy.IGNORED)
    private String floatBalance;

    /**
     * 固定价结算
     */
    @TableField( updateStrategy = FieldStrategy.IGNORED)
    private Long fixedBalance;

    /**
     * 审核时间
     */
    private String authDate;

    /**
     * 操作人
     */
    private Long opId;




    /**
     * 是否审核
     */
    private Integer isAuth;

    /**
     * 手续费
     */
    private String serviceCharge;

    /**
     * 开票浮动价结算
     */
    @TableField( updateStrategy = FieldStrategy.IGNORED)
    private String floatBalanceBill;

    /**
     * 开票固定价结算
     */
    @TableField( updateStrategy = FieldStrategy.IGNORED)
    private Long fixedBalanceBill;

    /**
     * 开票手续费
     */
    @TableField( updateStrategy = FieldStrategy.IGNORED)
    private String serviceChargeBill;

    /**
     * 服务商 审核状态：1.待审核  2.通过 3.驳回
     */
    private Integer serviceAuthState;

    /**
     * 服务商审核原因
     */
    private String serviceAuthRemark;

    /**
     * 合作状态 1：合作中  2：已解约
     */
    private Integer cooperationState;

    /**
     * 开票手续费浮动价结算
     */
    @TableField( updateStrategy = FieldStrategy.IGNORED)
    private String floatServiceChargeBill;

    /**
     * 不开票手续费浮动价结算
     */
    @TableField( updateStrategy = FieldStrategy.IGNORED)
    private String floatServiceCharge;

    /**
     * 现场价 1是 0 不是
     */
    private Integer localeBalanceState;


    /**
     * 是否共享（1.是，2.否）
     */
    private Integer isShare;


    /**
     * 油站电话
     */
    private String serviceCall;


    /**
     * 服务商名称
     */
    private String productName;

    /**
     * 简介
     */
    private String introduce;



    /**
     * 省份ID
     */
    private Integer provinceId;

    /**
     * 市编码ID
     */
    private Integer cityId;

    /**
     * 县区ID
     */
    private Integer countyId;

    /**
     * 详细地址
     */
    private String address;

}
