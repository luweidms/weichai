package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;

@Data
public class AccountBankRelDto  implements Serializable {

    private static final long serialVersionUID = 6158168989076998224L;

    private  String acctName;
    private  String acctNo;
    private  String pinganPayAcctId;
    private  Long lockBalance;
}
