package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName RefundInfoRepDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 11:20
 */
@Data
public class RefundInfoRepDto implements Serializable {
    /** 原提现银行返回流水 */
    private String origRespNo;

    /** 子商户编号 */
    private String mbrNo;

    /** 退票金额 */
    private String refundAmt;

    /** 退票原因 */
    private String refundNote;

    /** 原提现的目标绑定账户，只显示尾号 */
    private String accNo;

    /** 原提现摘要 */
    private String remark;
}
