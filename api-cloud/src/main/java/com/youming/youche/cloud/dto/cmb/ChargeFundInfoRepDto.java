package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName ChargeFundInfoRepDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:38
 */
@Data
public class ChargeFundInfoRepDto implements Serializable {
    /** 原请求流水，如果记录为系统自动识别，则由系统生成；否则为原调账请求流水 */
    private String origReqNo;

    /** 银行处理流水号（同通知9.2 */
    private String bizSeq;

    /** 入账子商户号 */
    private String mbrNo;

    /** 金额 */
    private String tranAmt;

    /** 入账日期，yyyyMMdd */
    private String tranDate;

    /** 入账时间-HHmmSS */
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

    /** 交易后余额同步状态，
     Y：已同步
     N：未同步 */
    private String balSyncStatus;

    /** 收方交易后余额，-1表示未同步到 */
    private String recvMbrBal;
}
