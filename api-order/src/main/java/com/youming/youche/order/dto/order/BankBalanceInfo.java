package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hzx
 * @date 2022/4/16 11:59
 */
@Data
public class BankBalanceInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String accId;
    private Double balance;
    private String thirdCustId;
    private String custName;
    private String acctNo;
    /**
     * 银行编号
     */
    private String bankCode;
    private Double bankBalance;
    private Double lockSum;

    private Integer isDefaultAcct;
    /**
     * 核销金额（分）
     */
    private Long payAmount;
    private Long receAmount;

    private String payAcctId;
    private String receAcctId;
    private Integer bankType;


    private String provinceName;
    private String cityName;
    private String branchName;

    private String receAmountYuan;

    private Double receBankBalance;
    private Double payBankBalance;

    private Double payUnDoBalance;

    private long relSeq;
    private Long provinceId;
    private Long cityId;

}
