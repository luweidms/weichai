package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DirverxDto implements Serializable {
    private Long userType;//用戶类型
    private Long collectAcctNo;
    private Long payAcctNo;//付款账户编号
    private Long acctNo;//账户编号
}
