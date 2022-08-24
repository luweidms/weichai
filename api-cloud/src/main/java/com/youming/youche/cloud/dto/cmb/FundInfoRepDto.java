package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName FundInfoRepDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:41
 */
@Data
public class FundInfoRepDto implements Serializable {
    /** 银行主机流水号 */
    private String bizSeq;

    /** 金额 */
    private String tranAmt;

    /** 入账日期，yyyyMMdd */
    private String tranDate;

    /** 入账时间，HHmmss */
    private String tranTime;

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

    /** 主机套号 */
    private String hostSet;

    /** 主机流水号 */
    private String hostSerial;
}
