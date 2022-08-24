package com.youming.youche.market.domain.facilitator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2022-01-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TenantProductRelVer extends BaseDomain {

    private static final long serialVersionUID = 1L;




    /**
     * 关系主键
     */

    private Long relId;

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
    @TableField(exist = false)
    private String stateName;

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
    private String floatBalance;

    /**
     * 固定价结算
     */
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
     * 操作时间
     */
    private String opDate;



    /**
     * 修改时间
     */
    private Long updateOpId;



    /**
     * 是否删除（0：否；1：是）
     */
    private Integer isDel;

    /**
     * 手续费
     */
    private String serviceCharge;

    /**
     * 开票浮动价结算
     */
    private String floatBalanceBill;

    /**
     * 开票固定价结算
     */
    private Long fixedBalanceBill;

    /**
     * 开票手续费
     */
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
     * 现场价 1是 0 不是
     */
    private Integer localeBalanceState;






    /**
     * 是否共享（1.是，2.否）
     */
    @TableField(exist = false)
    private Integer isShare;


    /**
     * 油站电话
     */
    @TableField(exist = false)
    private String serviceCall;


    /**
     * 服务商名称
     */
    @TableField(exist = false)
    private String productName;

    /**
     * 简介
     */
    @TableField(exist = false)
    private String introduce;



    /**
     * 省份ID
     */
    @TableField(exist = false)
    private Integer provinceId;

    /**
     * 省份ID
     */
    @TableField(exist = false)
    private String provinceName;

    /**
     * 市编码ID
     */
    @TableField(exist = false)
    private Integer cityId;

    /**
     * 市编码ID
     */
    @TableField(exist = false)
    private String cityName;

    /**
     * 县区ID
     */
    @TableField(exist = false)
    private Integer countyId;


    /**
     * 县区ID
     */
    @TableField(exist = false)
    private String countyName;

    /**
     * 详细地址
     */
    @TableField(exist = false)
    private String address;

}
