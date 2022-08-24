package com.youming.youche.market.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class VehicleAfterServingDto implements Serializable {

    private static final long serialVersionUID = 9141676951395353749L;

    // 1保养费2维修费
    private Integer state;

    // 金额
    private Long totalFee;

}
