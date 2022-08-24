package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseAcctInfoInDto implements Serializable {


    private static final long serialVersionUID = -4764966334173721159L;

    private String acctId;//支付中心账号
    private String password;//支付中心密码
}
