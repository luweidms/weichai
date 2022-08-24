package com.youming.youche.market.vo.facilitator;

import lombok.Data;

import java.io.Serializable;
@Data
public class ServiceProductVo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 站点id
     */
    private Long productId;
    /**
     * 站点名称
     */
    private String productName;
    /**
     * 租户
     */
    private Long tenantId;
    /**
     *租户id
     */
    private Long oilSourceTenantId;
    /**
     * 地址
     */
    private String address;
    /**
     *  '浮动价结算',
     */
    private  String floatBalance;
    /**
     * '固定价结算',
     */
    private Long fixedBalance;
    /**
     * '状态（1.有效、2.无效）',
     */
    private Integer state;

    private String stateName;
    /**
     * '审核状态（1.未审核、2.审核通过、3.审核未通过）',
     */
    private String authState;
    /**
     * 服务电话
     */
    private String serviceCall;
    /**
     * 省份id
     */
    private Integer provinceId;

    private String provinceName;
    /**
     * 市编码id
     */
    private Integer cityId;

    private String  cityName;
    /**
     * 县区id
     */
    private String countyId;

    private String countyName;
    /**
     * 是否认证？
     */
    private Integer isAuth;
    /**
     * 审核原因
     */
    private String authReason;
    /**
     * 关系主键
     */
    private Long relId;
    /**
     * 服务商名称
     */
    private String serviceName;
    /**
     * 服务商id
     */
    private Long serviceUserId;
    /**
     * '服务商类型（1.油站、2.维修、3.etc供应商）',
     */
    private Integer serviceType;
    /**
     * '开票浮动价结算',
     */
    private String floatBalanceBill;
    /**
     * '开票固定价结算',
     */
    private Long fixedBalanceBill;
    /**
     *  '现场价 1是 0 不是',
     */
    private Integer localeBalanceState;

    private String localeBalanceStateName;
    /**
     *  '是否共享（1.是，2.否）',
     */
    private Integer isShare;

    private String isShareName;
    /**
     *  '服务商 审核状态：1.待审核  2.通过 3.驳回',
     */
    private Integer serviceAuthState;

    private String serviceAuthStateName;
    /**
     * '是否有开票能力（1.有，2.无）',
     */
    private Integer isBillAbility;

    private String isBillAbilityName;
    /**
     * '是否开票（1.是、2.否）',
     */
    private Integer isBill;

    private String isBillName;
    /**
     * 油价
     */
    private Long originalPrice;
    /**
     * 手续费
     */
    private String serviceCharge;
    /**
     * '开票手续费',
     */
    private String serviceChargeBill;
    /**
     * 开票油价
     */
    private String oilPriceBill;

    private String oilPrice;

    private Integer hasVer;
}
