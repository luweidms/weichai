package com.youming.youche.cloud.constant;

/**
 * @ClassName CmbInftConst
 * @Description 添加描述
 * @Author zag
 * @Date 2022/3/2 16:45
 */
public class CmbIntfConst {

    public static class TranFunc {
        public static final String MERCHREG = "merchReg";
        public static final String MERCHREGQRY = "merchRegQry";
        public static final String ACCAUTHPRECHK = "accAuthPreChk";
        public static final String ACCAUTHREQ = "accAuthReq";
        public static final String ACCAUTHREQQRY = "accAuthReqQry";
        public static final String BNKACCBIND = "bnkAccBind";
        public static final String BNKACCLISTQRY = "bnkAccListQry";
        public static final String BNKACCCNL = "bnkAccCnl";
        public static final String ITAFUNDLISTQRY = "itaFundListQry";
        public static final String ITAFUNDADJUST = "itaFundAdjust";
        public static final String CHARGEFUNDLISTQRY = "chargeFundListQry";
        public static final String SMSCODEAPPLY = "smsCodeApply";
        public static final String ORDERFUNDRESERVE = "orderFundReserve";
        public static final String ORDERFUNDREVOKE = "orderFundRevoke";
        public static final String ORDERCHECKOUT = "orderCheckOut";
        public static final String ORDERTRANSFER = "orderTransfer";
        public static final String ITATRANQRY = "itaTranQry";
        public static final String TRANLISTQRY = "tranListQry";
        public static final String SINGLECASHBATCHSEP = "singleCashBatchSep";
        public static final String SINGLECASHBATCHSEPQRY = "singleCashBatchSepQry";
        public static final String SETTRANSFER = "setTransfer";
        public static final String WITHDRAW = "withdraw";
        public static final String REFUNDLISTQRY = "refundListQry";
        public static final String RECONFILEDOWNLOAD = "reconFileDownload";
        public static final String RECEIPTFILEDOWNLOAD = "receiptFileDownload";
        public static final String TRADEFILEDOWNLOAD = "tradeFileDownload";
        public static final String MBRBASEINFOQRY = "mbrBaseInfoQry";
        public static final String MBRBALQRY = "mbrBalQry";
        public static final String TOTALORDERFUNDQRY = "totalOrderFundQry";
        public static final String MGRINFOCHG = "mgrInfoChg";
        public static final String MBRINFOCHG = "mbrInfoChg";

        //回调接口
        public static final String MERCHREG_CALLBACK = "merchRegCallBack";
        public static final String MBRCHARGEFUND_CALLBACK = "mbrChargeFundCallBack";
        public static final String ITAFUND_CALLBACK = "itaFundCallBack";
        public static final String WITHDRAW_CALLBACK="withdrawCallBack";
    }

    /**
     * 接口响应码
     */
    public static class ResponseCode{
        /**
         * 10000
         */
        public static final String Code_10000 = "10000";
        /**
         * 10001
         */
        public static final String Code_10001 = "10001";
        /**
         * 20001
         */
        public static final String Code_20001 = "20001";
    }

    /**
     * 交易类型
     */
    public static class TranType {
        /**
         * 充值(调帐)
         */
        public static final String AC = "AC";
        /**
         * 转帐
         */
        public static final String BP = "BP";
        /**
         * 提现
         */
        public static final String WD = "WD";
    }

    /**
     * 交易状态
     */
    public static class TranStatus {
        /**
         * 处理中
         */
        public static final String T = "T";
        /**
         * 交易成功
         */
        public static final String Y = "Y";
        /**
         * 交易失败
         */
        public static final String F = "F";
        /**
         * 提现已受理
         */
        public static final String R = "R";
        /**
         * 响应超时
         */
        public static final String O = "O";
    }

    /**
     * 交易后余额同步状态
     */
    public static class BalSyncStatus {
        /**
         * 已同步
         */
        public static final String Y = "Y";
        /**
         * 未同步
         */
        public static final String N = "N";
    }

    /**
     * 入帐模式
     */
    public static class InjectionMethod {
        /**
         * 系统自动入帐
         */
        public static final String AUTOMATIC = "1";
        /**
         * 手动帐帐
         */
        public static final String MANUAL = "2";
        /**
         * 匿名入帐
         */
        public static final String ANONYMITY = "3";
    }

    /**
     * 账户类型
     */
    public static class AccType {
        /**
         * 对公
         */
        public static final String C = "C";
        /**
         * 个人
         */
        public static final String P = "P";
    }

    /**
     * 证照类型
     */
    public static class CertType{
        /**
         * 个体身份证
         */
        public static final String P01 = "P01";
        /**
         * 个体工商户，企业
         */
        public static final String C35 = "C35";
    }

    /**
     * 注册状态
     */
    public static class MerchRegStatus {
        /**
         * 受理中
         */
        public static final String I = "I";
        /**
         * 成功
         */
        public static final String S = "S";
        /**
         * 失败
         */
        public static final String F = "F";

    }

    /**
     * 回调接口类型
     */
    public static class CallBackType {
        /**
         * 商户进件回调接口
         */
        public static final String A = "A";
        /**
         * 子商户注资回调接口
         */
        public static final String B = "B";
        /**
         * 匿名资金回调接口
         */
        public static final String C = "C";
        /**
         * 提现回调接口
         */
        public static final String H = "H";
    }

}
