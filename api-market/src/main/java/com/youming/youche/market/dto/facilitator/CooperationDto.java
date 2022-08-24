package com.youming.youche.market.dto.facilitator;

import lombok.Data;

import java.io.Serializable;
@Data
public class CooperationDto implements Serializable {
    /**
     * 站点名称
     */
    private String productName;
    /**
     * 是否有开票能力（1.有，2.无）
     */
    private Integer isBillAbility;

    private String countyName;

    /**
     * 浮动价结算
     */
    private String floatBalance;

    private String cityName;

    /**
     * 固定价结算
     */
    private Long fixedBalance;

    /**
     * 手续费
     */
    private String serviceCharge;

    private String provinceName;

    /**
     * 浮动价结算(开票)
     */
    private String floatBalanceBill;

    /**
     * `固定价结算`(开票)
     */
    private Long fixedBalanceBill;

    /**
     * '开票手续费',
     */
    private String serviceChargeBill;

    /**
     * 现场价 1是 0 不是
     */
    private Integer localeBalanceState;


    private Long oilPrice;

    /**
     * 服务商用户id
     */
    private Long serviceUserId;

    private Long productId;

    /**
     * 常用办公地址
     */
    private String address;
    /**
     * 省id
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


}
