package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class OilRechargeAccountDetailsVo implements Serializable {

    private static final long serialVersionUID = 1221697044008246505L;

    private  String pinganPayAcctId;
    private  Long tenantId;
}
