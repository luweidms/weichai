package com.youming.youche.market.dto.user;

import lombok.Data;

import java.io.Serializable;
@Data
public class AccountBankByTenantIdDto implements Serializable {

    private static final long serialVersionUID = -8619359492980032255L;

    /**
     *收款账号
     */
    private String acctNo;
    /**
     * 账号名称
     */
    private String acctName;


}
