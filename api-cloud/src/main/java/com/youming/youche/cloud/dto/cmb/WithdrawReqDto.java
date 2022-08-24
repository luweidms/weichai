package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName WithdrawReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 13:47
 */
@Data
public class WithdrawReqDto implements Serializable {
    /** 请求方通讯流水 */
    private String reqNo;

    /** 平台编号 */
    private String platformNo;

    /** 提现子商户，平台子户提现时，传值请参考参数规定中的subAccNo */
    private String mbrNo;

    /** 提现金额 */
    private String tranAmt;

    /** 提现卡号，如果不填则提现到子商户默认结算账户上(如果未设置默认账号则报错)；如果填了则进行已绑定校验 */
    private String accNo;

    /** 支取方式:
     1:无校验
     2:短信验证码，需要附带短信包 */
    private String wdwTyp;

    /** 摘要 */
    private String remark;

    /** 验证信息 */
    private AuthInfoReqDto authInfo;
}
