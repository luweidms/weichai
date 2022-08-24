package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName WithdrawRepDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 13:46
 */
@Data
public class WithdrawRepDto implements Serializable {
    /** 同请求接口中reqNo流水 */
    private String reqNo;

    /** 银行处理流水 */
    private String respNo;

    /** 平台编号 */
    private String platformNo;

    /** 提现子商户 */
    private String mbrNo;

    /** 提现金额 */
    private String tranAmt;

    /** 提现的目标绑定账户，只显示尾号 */
    private String accNo;

    /** 摘要 */
    private String remark;

    /** 交易日期 */
    private String tranDate;

    /** 交易时间 */
    private String tranTime;
}
