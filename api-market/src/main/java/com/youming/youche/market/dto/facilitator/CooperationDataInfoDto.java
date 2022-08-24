package com.youming.youche.market.dto.facilitator;

import lombok.Data;

import java.io.Serializable;

@Data
public class CooperationDataInfoDto implements Serializable {

    /**
     * 现场价 1是 0 不是
     */
    private Integer localeBalanceState;

    private String localeBalanceStateName;
    /**
     * 油价
     */
    private Long oilPrice;

    /**
     * 站点名称
     */
    private String productName;

    /**
     * 站点ID
     */
    private Long productId;
    /**
     * 账期
     */
    private String paymentDays;

    /**
     * 浮动价结算
     */
    private String floatBalance;

    private String paymentDaysType;

    /**
     * 固定价结算
     */
    private Long fixedBalance;

    private String paymentDaysOld;

    private String paymentDaysOldType;

    private String floatBalanceOld;

    private Long fixedBalanceOld;


}
