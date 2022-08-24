package com.youming.youche.market.dto.youca;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author XXX
 * @since 2022-03-17
 */
@Data
public class ConsumeOilFlowDtos implements Serializable {

    /**
     * 金额
     */
    private Long amount;

    /**
     * 回扣金额
     */
    private String fleetRebateAmount;

    /**
     * 加油升数
     */
    private Float oilRise;
}
