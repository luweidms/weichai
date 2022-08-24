package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PayManagerWXDto implements Serializable {
    private Integer state;
    private Long orgId;
    private Long payAmt;
    private Integer isNeedBill;
    private Long payId;
}
