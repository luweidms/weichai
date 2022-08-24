package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName SmsCodeApplyRepDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 11:31
 */
@Data
public class SmsCodeApplyRepDto implements Serializable {
    /** 同请求接口中reqNo流水 */
    private String reqNo;

    /** 银行处理流水 */
    private String respNo;

    /** 平台编号 */
    private String platformNo;

    /** 业务类型：
     OC：结单
     BP：可用余额支付
     WD：提现 */
    private String tranType;

    /** 接收手机号 */
    private String mobile;

    /** 可重新申请的时间点（间隔为1分钟） */
    private String reApplyTime;

    /** 验证码过期时间点（间隔为5分钟） */
    private String expireTime;

    /** 相关子商户号 */
    private String mbrNo;

    /** 相关子商户名称 */
    private String mbrName;

    /** 涉及金额 */
    private String tranAmt;
}
