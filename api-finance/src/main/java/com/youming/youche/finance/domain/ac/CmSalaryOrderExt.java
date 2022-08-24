package com.youming.youche.finance.domain.ac;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
* <p>
    * 
    * </p>
* @author zengwen
* @since 2022-04-22
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class CmSalaryOrderExt extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 渠道类型
     */
    private String channelType;

    /**
     *
     */
    private LocalDateTime createDate;

    /**
     * 公里数
     */
    private Long formerMileage;

    /**
     *
     */
    private Long opId;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 节油奖
     */
    private Long saveOilBonus;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 本趟工资
     */
    private Long tripSalaryFee;

    /**
     *
     */
    private LocalDateTime updateDate;

    /**
     *
     */
    private Long updateOpId;


}
