package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;

@Data
public class BankFlowDetailsAppOutDto implements Serializable {

    private static final long serialVersionUID = 5838973532023340684L;
    private String acctNoName;//账户
    private String acctNameInType;
    private String acctNameOutType;
    private  String  month;
}
