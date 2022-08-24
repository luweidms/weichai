package com.youming.youche.finance.domain.vehicle;

import lombok.Data;

import java.io.Serializable;

@Data
public class VehicleMiscellaneouFeeDto implements Serializable {

    private static final long serialVersionUID = -5377535378949847271L;

    // 维修费 - 64、保养费 - 65、油费 - 62、路桥费 - 63、杂费 - 67
    private Integer type;

    // 金额
    private Long applyAmountSum;

}
