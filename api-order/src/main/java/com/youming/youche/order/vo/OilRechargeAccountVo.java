package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class OilRechargeAccountVo  implements Serializable {


    private static final long serialVersionUID = -8039846218235085810L;
    private  Long id;
    private String pinganAccId;
    private Long amount;
    private Long creditAmount;
    private Long serviceId;
    private  Integer oilRechargeBillType;
    private  Boolean  useCredit;
}
