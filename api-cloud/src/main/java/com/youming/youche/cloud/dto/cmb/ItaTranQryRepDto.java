package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName ItaTranQryRepDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:46
 */
@Data
public class ItaTranQryRepDto implements Serializable {
    /** 同请求接口中origReqNo流水 */
    private String origReqNo;

    /** 银行处理流水 */
    private String respNo;

    /** 平台编号 */
    private String platformNo;

    /** 原交易银行返回流水 */
    private String origRespNo;

    /** 交易类型，同请求 */
    private String origTranType;

    /** 原交易状态
     X：初始化
     T：处理中
     Y：成功
     F：失败 */
    private String origTranStatus;

    /** 原子商户号，同下单一致 */
    private String origPayMbrNo;

    /** 转账（冻结）金额 */
    private String origTranAmt;

    /** 收方子商户编号 */
    private String origRecvMbrNo;

    /** 订单编号 */
    private String origOrderNo;

    /** 原摘要 */
    private String origRemark;

    /** 交易日期 */
    private String origTranDate;

    /** 交易完成时间 */
    private String origTranTime;

    /** 中间账户，套账交易才会有值 */
    private String origMidMbrNo;

    /** 付方回单查询流水 付方->中间账户 套账交易才有 */
    private String receiptPaySerial;

    /** 收方回单查询流水 中间账户->收方 套账交易才有 */
    private String receiptRecSerial;

    /** 交易后余额同步状态，
     Y：已同步
     N：未同步 */
    private String balSyncStatus;

    /** 付方交易后余额，-1表示无值或未同步到 */
    private String payMbrBal;

    /** 中间方交易后余额，-1表示无值或未同步到 */
    private String midMbrBal;

    /** 收方交易后余额，-1表示无值或未同步到 */
    private String recvMbrBal;
}
