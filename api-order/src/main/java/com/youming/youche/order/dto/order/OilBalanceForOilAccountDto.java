package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;

@Data
public class OilBalanceForOilAccountDto implements Serializable {

    private static final long serialVersionUID = 8009742423251321451L;

    /**
     * 不开票金额
     */
    private Long custOilBalance;

    /**
     * 抵扣油余额
     */
    private Long deductOilBalance;

    /**
     * 非抵扣油余额
     */
    private Long nonDeductOilBalance;

    /**
     * 返利油（已开票）
     */
    private Long rebateOilBalance;

    /**
     * 转移油余额（已开票）
     */
    private Long transferOilBalance;

    /**
     * 抵扣油票余额(运输专票)
     */
    private Long billLookUpBalance;
}
