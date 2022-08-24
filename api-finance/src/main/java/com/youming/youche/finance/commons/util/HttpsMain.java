package com.youming.youche.finance.commons.util;

/**
 * @author zengwen
 * @date 2022/4/7 14:38
 */
public class HttpsMain {

    /**
     *
     * 0发起成功 1发起失败 2银行打款失败 3提现成功(打款成功)
     */
    /**发起成功 */
    public static final String respCodeZero = "0";
    /**银行打款失败 */
    public static final String respCodeFail = "2";
    /**提现成功(打款成功) */
    public static final String respCodeSuc = "3";
    /**网络超时 */
    public static final String netTimeOutFail = "4";
    /**提现记录失效*/
    public static final String respCodeInvalid = "5";
    /**不绑卡收款针对司机收款（待收款）**/
    public static final String respCodeCollection = "6";
}
