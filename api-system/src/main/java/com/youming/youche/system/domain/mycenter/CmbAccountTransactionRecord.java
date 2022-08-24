package com.youming.youche.system.domain.mycenter;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @ClassName CmbAccountTransactionRecord
 * @Description 添加描述
 * @Author zag
 * @Date 2022/2/20 15:54
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CmbAccountTransactionRecord extends BaseDomain {
//    /** id */
//    @TableField("id")
//    private Long id;
    /** 付款用户Id */
    @TableField("payUserId")
    private Long payUserId;
    /** 收款用户Id */
    @TableField("recvUserId")
    private Long recvUserId;
    /** 支付业务Id */
    @TableField("payoutId")
    private Long payoutId;
    /** 交易请求流水号 */
    @TableField("reqNo")
    private String reqNo;
    /** 交易处理流水号 */
    @TableField("respNo")
    private String respNo;
    /** 付款方帐户Id */
    @TableField("payAccountId")
    private Long payAccountId;
    /** 收款方帐户Id */
    @TableField("recvAccountId")
    private Long recvAccountId;
    /** 付款方子商户号 */
    @TableField("payMbrNo")
    private String payMbrNo;
    /** 付款方子商户名称 */
    @TableField("payMbrName")
    private String payMbrName;
    /** 充值银行帐号 */
    @TableField("payAccNo")
    private String payAccNo;
    /** 充值银行户名 */
    @TableField("payAccName")
    private String payAccName;
    /** 充值开户行 */
    @TableField("payEab")
    private String payEab;
    /** 收款方子商户号 */
    @TableField("recvMbrNo")
    private String recvMbrNo;
    /** 收款方子商户名称 */
    @TableField("recvMbrName")
    private String recvMbrName;
    /** 提现银行帐号 */
    @TableField("recvAccNo")
    private String recvAccNo;
    /** 提现银行户名 */
    @TableField("recvAccName")
    private String recvAccName;
    /** 提现开户行 */
    @TableField("recvEab")
    private String recvEab;
    /** 交易完成时间 */
    @TableField("tranTime")
    private String tranTime;
    /** 交易金额 */
    @TableField("tranAmt")
    private String tranAmt;
    /** 交易类型（AC：调帐（充值）；BP：转帐；WD：提现） */
    @TableField("tranType")
    private String tranType;
    /** 交易状态（Y：交易成功；F：交易失败） */
    @TableField("tranStatus")
    private String tranStatus;
    /** 注资模式（1：自动注资（充值）；2：手动调帐（调帐）） */
    @TableField("injectionMethod")
    private String injectionMethod;
    /** 备注 */
    @TableField("remark")
    private String remark;
    /** 退票日期 */
    @TableField("clearDate")
    private String clearDate;
    /** 退票原因 */
    @TableField("refundNote")
    private String refundNote;
    /** 退票金额 */
    @TableField("refundAmt")
    private String refundAmt;

}
