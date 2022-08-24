package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName ItaFundCallBackDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:44
 */
@Data
public class ItaFundCallBackDto implements Serializable {
    /** 平台编号 */
    private String platformNo;

    /** 银行主机流水号 */
    private String bizSeq;

    /** 入账金额 */
    private String chargeAmt;

    /** 入账时间 */
    private String chargeTime;

    /** 对手方银行账号 */
    private String oppAccNo;

    /** 对手方银行户名 */
    private String oppAccName;

    /** 银行摘要代码4位,可以帮助识别业务 */
    private String txtCode;

    /** 入账附言 */
    private String remark;

    /** 对手方开户行 */
    private String oppEab;

    /** 对手方开户地 */
    private String oppEaa;

    /** 对手方联行号 */
    private String oppBankNo;
}
