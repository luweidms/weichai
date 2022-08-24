package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName SmsCodeApplyReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 13:42
 */
@Data
public class SmsCodeApplyReqDto implements Serializable {
    /** 请求方流水 */
    private String reqNo;

    /** 平台编号 */
    private String platformNo;

    /** 业务类型：
     OC：结单
     BP：可用余额支付
     WD：提现
     ST:套账
     SS: 分账，仅用于申请验证码 */
    private String tranType;

    /** 关联子商户号 */
    private String mbrNo;

    /** 关联子商户名称 */
    private String mbrName;

    /** 交易金额 */
    private String tranAmt;
}
