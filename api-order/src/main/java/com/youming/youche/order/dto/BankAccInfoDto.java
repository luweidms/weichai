package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

import static com.youming.youche.conts.EnumConsts.BALANCE_BANK_TYPE.*;

@Data
public class BankAccInfoDto implements Serializable {

    private String accBankNo;//账户
    /**
     * 银行编号
     */
    private String bankCode;
    /**
     * 省份
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 银行名称
     */
    private String accBankName;
    /**
     * 账号
     */
    private String accNo;
    /**
     * 是否系统自动打款,0手动核销，1系统自动打款
     */
    private Integer isAutomatic;
    /**
     * 账户类型 1母卡 2子卡
     */
    private Integer accountType;
    /**
     * 账户类型：0 个人 1 对公
     */
    private Integer bankType;


    //设置对私付款账户
    public void setPrivatePinganAcctIdN(PinganBankInfoOutDto pinganBankInfoOut){
        this.setAccNo(pinganBankInfoOut.getPrivatePinganAcctIdN());
        this.setAccountType(PRIVATE_PAYABLE_ACCOUNT);
        this.setAccBankNo(pinganBankInfoOut.getPrivateAcctNo());
        this.setAccBankName(pinganBankInfoOut.getPrivateAcctName());
        this.setBankCode(pinganBankInfoOut.getPrivateBankCode());
    }

    //设置对私收款账号
    public void setPrivatePinganAcctIdM(PinganBankInfoOutDto pinganBankInfoOut){
        this.setAccNo(pinganBankInfoOut.getPrivatePinganAcctIdM());
        this.setBankType(PRIVATE_RECEIVABLE_ACCOUNT);
        this.setAccBankNo(pinganBankInfoOut.getPrivateAcctNo());
        this.setAccBankName(pinganBankInfoOut.getPrivateAcctName());
        this.setBankCode(pinganBankInfoOut.getPrivateBankCode());
    }

    //设置对公付款账户
    public void setCorporatePinganAcctIdN(PinganBankInfoOutDto pinganBankInfoOut){
        this.setAccNo(pinganBankInfoOut.getCorporatePinganAcctIdN());
        this.setAccBankNo(pinganBankInfoOut.getCorporateAcctNo());
        this.setAccBankName(pinganBankInfoOut.getCorporateAcctName());
        this.setBankCode(pinganBankInfoOut.getCorporateBankCode());
        this.setAccountType(BUSINESS_PAYABLE_ACCOUNT);
    }

    //设置对公收款账户
    public void setCorporatePinganAcctIdM(PinganBankInfoOutDto pinganBankInfoOut){
        this.setAccNo(pinganBankInfoOut.getCorporatePinganAcctIdM());
        this.setAccBankNo(pinganBankInfoOut.getCorporateAcctNo());
        this.setAccBankName(pinganBankInfoOut.getCorporateAcctName());
        this.setBankCode(pinganBankInfoOut.getCorporateBankCode());
        this.setBankType(BUSINESS_RECEIVABLE_ACCOUNT);
    }
}
