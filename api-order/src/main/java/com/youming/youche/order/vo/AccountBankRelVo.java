package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AccountBankRelVo  implements Serializable {

    private static final long serialVersionUID = 6055087334602048915L;

    private Long  amount;
    /**
     * 账户类型 1母卡 2子卡
     */
    private Integer accountType;
    private String mesCode;
    private String serialNo;
    private  String accountNo;

    //小程序 提现 入参
    private  Double amountDouble;
    private String payPasswd; //支付密码

    private Long  advanceAmount;

    //获取银行流水下载地址接口 入参
    private Long userId;
    private String beginDate;
    private String endDate;
    private  String acctIdIn;
    private String acctIdOut;
}
