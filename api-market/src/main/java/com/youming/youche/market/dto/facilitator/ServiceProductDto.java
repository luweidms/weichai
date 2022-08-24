package com.youming.youche.market.dto.facilitator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceProductDto implements Serializable {

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
     *
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
     * 公司地址
     */
    private String companyAddress;
    /**
     * '固定价结算',
     */
    private Long fixedBalance;
    /**
     * '状态（1.有效、2.无效）',
     */
    private Integer state;
    /**
     * '审核状态（1.未审核、2.审核通过、3.审核未通过）',
     */
    private Integer authState;
    /**
     * 服务电话
     */
    private String serviceCall;
    /**
     * 省份id
     */
    private Integer provinceId;
    /**
     * 省份名称
     */
    private String provinceName;
    /**
     * 市编码id
     */
    private Integer cityId;
    /**
     * 市名称
     */
    private String cityName;
    /**
     * 县区id
     */
    private Integer countyId;
    /**
     * 县名称
     */
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
    /**
     *  '是否共享（1.是，2.否）',
     */
    private Integer isShare;
    /**
     *  '服务商 审核状态：1.待审核  2.通过 3.驳回',
     */
    private Integer serviceAuthState;
    /**
     * '是否有开票能力（1.有，2.无）',
     */
    private Integer isBillAbility;
    /**
     * '是否开票（1.是、2.否）',
     */
    private Integer isBill;
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
     * 油價
     */
    private Long oilPrice;

    private String productInfo;

}
