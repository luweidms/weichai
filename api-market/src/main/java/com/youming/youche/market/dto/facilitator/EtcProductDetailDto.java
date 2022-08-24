package com.youming.youche.market.dto.facilitator;

import com.youming.youche.market.domain.facilitator.ServiceProductEtc;
import lombok.Data;

@Data
public class EtcProductDetailDto extends ServiceProductEtc {

    /***
     * 正常还款收款方
     */
    private String nrServiceName;
    private String nrAccName;
    private String nrAccNo;
    private String nrAcctTypeName;
    private String nrBankName;
    private String nrBranchName;

    /**
     * 超期还款
     */
    private String orServiceName;
    private String orAccName;
    private String orAccNo;
    private String orAcctTypeName;
    private String orBankName;
    private String orBranchName;

    //平台服务费
    private String pltSerFeeTypeName;
}
