package com.youming.youche.finance.domain.ac;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
* <p>
    * 
    * </p>
* @author zengwen
* @since 2022-04-30
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OrderDriverSubsidyInfo extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;


    private Long orderId;

    private Long driverUserId;

    private Long subsidyFeeSum;
    private Integer subsidyFeeSumState;

    /**
     * 已结算金额
     */
    private Long settleMoney;


}
