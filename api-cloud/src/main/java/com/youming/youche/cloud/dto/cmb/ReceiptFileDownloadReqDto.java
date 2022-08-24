package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName ReceiptFileDownloadReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 11:19
 */
@Data
public class ReceiptFileDownloadReqDto implements Serializable {
    /** 平台编号 */
    private String platformNo;

    /** 原交易银行处理流水。交易类型为套账时，需要传入付方或收方流水 */
    private String origRespNo;

    /** 原交易类型：
     OC：结单
     BP：可用余额支付
     WD：提现
     AC：调账（充值）
     SE：分账
     ST：套账 */
    private String origTranType;
}
