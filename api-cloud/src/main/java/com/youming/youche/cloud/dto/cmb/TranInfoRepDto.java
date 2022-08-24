package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName TranInfoRepDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 13:45
 */
@Data
public class TranInfoRepDto implements Serializable {
    /** 原交易请求流水 */
    private String origReqNo;

    /** 原交易银行返回流水 */
    private String origRespNo;

    /** 业务类型：
     OD：下单
     OR：下单撤销
     OC：结单
     BP：可用余额支付
     WD：提现
     AC：调账
     ST:套账 */
    private String origTranType;

    /** 原交易状态
     X：初始化
     T：处理中
     Y：成功
     F：失败 */
    private String origTranStatus;

    /** 转账（冻结）金额 */
    private String origTranAmt;

    /** 付方（冻结）子商户号 */
    private String origPayMbrNo;

    /** 收方子商户号 */
    private String origRecvMbrNo;

    /** 交易日期 */
    private String origTranDate;

    /** 交易时间 */
    private String origTranTime;

    /** 订单编号 */
    private String origOrderNo;

    /** 摘要 */
    private String origRemark;

    /** 中间子商户号,套账交易才有值 */
    private String origMidMbrNo;

    /** 付方回单查询流水 付方->中间账户 套账交易才有 */
    private String receiptPaySerial;

    /** 收方回单查询流水 中间账户->收方 套账交易才有 */
    private String receiptRecSerial;

    /** 交易后余额是否同步成功，
     Y：是
     N：否 */
    private String balSyncStatus;

    /** 付方交易后余额，-1表示无值或未同步到 */
    private String payMbrBal;

    /** 中间方交易后余额，-1表示无值或未同步到 */
    private String midMbrBal;

    /** 收方交易后余额，-1表示无值或未同步到 */
    private String recvMbrBal;
}
