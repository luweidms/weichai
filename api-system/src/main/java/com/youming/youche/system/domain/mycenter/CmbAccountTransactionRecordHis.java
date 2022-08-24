package com.youming.youche.system.domain.mycenter;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
* <p>
    * 招行交易记录历史表
    * </p>
* @author zag
* @since 2022-04-24
*/
@Data
    @EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CmbAccountTransactionRecordHis extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 交易请求流水号
     */
    @TableField("origReqNo")
    private String origReqNo;

    /**
     * 交易处理流水号
     */
    @TableField("origRespNo")
    private String origRespNo;

    /**
     * 业务类型：OD：下单 OR：下单撤销 OC：结单 BP：可用余额支付 WD：提现 AC：调账 ST:套账
     */
    @TableField("origTranType")
    private String origTranType;

    /**
     * 原交易状态 X：初始化 T：处理中 Y：成功 F：失败
     */
    @TableField("origTranStatus")
    private String origTranStatus;

    /**
     * 转账（冻结）金额
     */
    @TableField("origTranAmt")
    private String origTranAmt;

    /**
     * 付方（冻结）子商户号
     */
    @TableField("origPayMbrNo")
    private String origPayMbrNo;

    /**
     * 收方子商户号
     */
    @TableField("origRecvMbrNo")
    private String origRecvMbrNo;

    /**
     * 交易日期
     */
    @TableField("origTranDate")
    private String origTranDate;

    /**
     * 交易时间
     */
    @TableField("origTranTime")
    private String origTranTime;

    /**
     * 订单编号
     */
    @TableField("origOrderNo")
    private String origOrderNo;

    /**
     * 摘要
     */
    @TableField("origRemark")
    private String origRemark;

    /**
     * 中间子商户号
     */
    @TableField("origMidMbrNo")
    private String origMidMbrNo;

    /**
     * 付方回单查询流水
     */
    @TableField("receiptPaySerial")
    private String receiptPaySerial;

    /**
     * 收方回单查询流水
     */
    @TableField("receiptRecSerial")
    private String receiptRecSerial;

    /**
     * 交易后余额是否同步成功，Y：是 N：否
     */
    @TableField("balSyncStatus")
    private String balSyncStatus;

    /**
     * 付方交易后余额
     */
    @TableField("payMbrBal")
    private String payMbrBal;

    /**
     * 中间方交易后余额
     */
    @TableField("midMbrBal")
    private String midMbrBal;

    /**
     * 收方交易后余额
     */
    @TableField("recvMbrBal")
    private String recvMbrBal;


}
