package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PinganBankInfoOutDto implements Serializable {
    private static final long serialVersionUID = -7857412212186691153L;
    private Long userId;
    private String corporatePinganAcctIdM;//对公平安收款账户
    private String corporatePinganAcctIdN;//对公平安付款账户
    private String corporateAcctName;//对公账户名称
    private String corporateAcctNo;//对公银行卡号
    private String corporateBankCode;//对公银行名称
    private String privatePinganAcctIdM;//对私平安收款账户
    private String privatePinganAcctIdN;//对私平安付款账户
    private String privateAcctName;//对私账户名称
    private String privateAcctNo;//对私银行卡号
    private String privateBankCode;//对私银行名称
    private String servicePinganAcctIdM;//票据服务平安收款账户
    private String servicePinganAcctIdN;//票据服务平安付款账户
    private String serviceAcctName;//票据服务账户名称
    private String serviceAcctNo;//票据银行卡号
    private String serviceBankCode;//票据银行名称
    private Integer isDefaultAcct;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCorporatePinganAcctIdM() {
        return corporatePinganAcctIdM;
    }

    public void setCorporatePinganAcctIdM(String corporatePinganAcctIdM) {
        this.corporatePinganAcctIdM = corporatePinganAcctIdM;
    }

    public String getCorporatePinganAcctIdN() {
        return corporatePinganAcctIdN;
    }

    public void setCorporatePinganAcctIdN(String corporatePinganAcctIdN) {
        this.corporatePinganAcctIdN = corporatePinganAcctIdN;
    }

    public String getCorporateAcctName() {
        return corporateAcctName;
    }

    public void setCorporateAcctName(String corporateAcctName) {
        this.corporateAcctName = corporateAcctName;
    }

    public String getCorporateAcctNo() {
        return corporateAcctNo;
    }

    public void setCorporateAcctNo(String corporateAcctNo) {
        this.corporateAcctNo = corporateAcctNo;
    }

    public String getCorporateBankCode() {
        return corporateBankCode;
    }

    public void setCorporateBankCode(String corporateBankCode) {
        this.corporateBankCode = corporateBankCode;
    }

    public String getPrivatePinganAcctIdM() {
        return privatePinganAcctIdM;
    }

    public void setPrivatePinganAcctIdM(String privatePinganAcctIdM) {
        this.privatePinganAcctIdM = privatePinganAcctIdM;
    }

    public String getPrivatePinganAcctIdN() {
        return privatePinganAcctIdN;
    }

    public void setPrivatePinganAcctIdN(String privatePinganAcctIdN) {
        this.privatePinganAcctIdN = privatePinganAcctIdN;
    }

    public String getPrivateAcctName() {
        return privateAcctName;
    }

    public void setPrivateAcctName(String privateAcctName) {
        this.privateAcctName = privateAcctName;
    }

    public String getPrivateAcctNo() {
        return privateAcctNo;
    }

    public void setPrivateAcctNo(String privateAcctNo) {
        this.privateAcctNo = privateAcctNo;
    }

    public String getPrivateBankCode() {
        return privateBankCode;
    }

    public void setPrivateBankCode(String privateBankCode) {
        this.privateBankCode = privateBankCode;
    }

    public String getServicePinganAcctIdM() {
        return servicePinganAcctIdM;
    }

    public void setServicePinganAcctIdM(String servicePinganAcctIdM) {
        this.servicePinganAcctIdM = servicePinganAcctIdM;
    }

    public String getServicePinganAcctIdN() {
        return servicePinganAcctIdN;
    }

    public void setServicePinganAcctIdN(String servicePinganAcctIdN) {
        this.servicePinganAcctIdN = servicePinganAcctIdN;
    }

    public String getServiceAcctName() {
        return serviceAcctName;
    }

    public void setServiceAcctName(String serviceAcctName) {
        this.serviceAcctName = serviceAcctName;
    }

    public String getServiceAcctNo() {
        return serviceAcctNo;
    }

    public void setServiceAcctNo(String serviceAcctNo) {
        this.serviceAcctNo = serviceAcctNo;
    }

    public String getServiceBankCode() {
        return serviceBankCode;
    }

    public void setServiceBankCode(String serviceBankCode) {
        this.serviceBankCode = serviceBankCode;
    }

    public Integer getIsDefaultAcct() {
        return isDefaultAcct;
    }

    public void setIsDefaultAcct(Integer isDefaultAcct) {
        this.isDefaultAcct = isDefaultAcct;
    }
}
