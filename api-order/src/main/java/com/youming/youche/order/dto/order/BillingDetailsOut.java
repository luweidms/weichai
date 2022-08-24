package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;
@Data
public class BillingDetailsOut  implements Serializable {
    private static final long serialVersionUID = -2983373500512530755L;

    private Long alreadyBillingAmuont;
    private Long amount;
    private Long waitBillingAmount;
    private String fleetName;
    private String month;
    private Long tenantId;
}
