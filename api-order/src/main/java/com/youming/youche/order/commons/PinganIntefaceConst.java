package com.youming.youche.order.commons;

public class PinganIntefaceConst {
    public static class Inteface_6110 {
        //接口请求的功能标志
        public static int FUNCFLAG_MEMBERSHIP_TRANSACTION = 2;//会员间交易
        public static int FUNCFLAG_WITHDRAW_CASH = 3;//提现
        public static int FUNCFLAG_Recharge = 4;//充值

    }
    public static class Inteface_6150 {
        //接口请求的功能标志 1为查询当日数据，0查询历史数据
        public static int FUNCFLAG_SAME_DAY = 1;//当天
        public static int FUNCFLAG_HISTORY = 0;//历史

        //入账类型
        public static String TRANTYPE_MEMBERSHIP_RECHARGE = "02";//会员充值
        public static String TRANTYPE_CAPITAL_ACCOUNT = "03";//资金挂账

        //处理状态
        public static int STAUTS_HANDLED = 1;//已处理
        public static int STAUTS_UNTREATED = 0;//初始状态
        public static int STAUTS_ERROR = 2;//處理失敗
        public static int STAUTS_STARTING = 3;//处理中

        public static int DEAL_HANGUP = 1;//挂账
        public static int DEAL_REFUND = 2;//退款
    }

    public static class RESP_BANK_CODE{
        //余额不足
        public static  final double BALANCE_NOT_ENOUGH = 2.1;
        //其他错误
        public static  final double BANK_OTHER_ERROR = 2.2;
        //本地业务异常
        public static  final double LOCAL_BUSI_ERROR = 2.3;
    }
    /**
     * 平安对账文件类型
     */
    public static class FILE_TYPE {
        /*
         * 充值
         */
        public static final String CZ = "CZ";
        /*
         * 提现
         */
        public static final String TX = "TX";
        /*
         * 交易
         */
        public static final String JY = "JY";
        /*
         * 余额
         */
        public static final String YE = "YE";
        /*
         * 合约
         */
        public static final String HY = "HY";

    }
    /*
     * 平安对账文件
     */
    public static class RECONCILIATION_FILE {
        /*
         * 文件保存路径
         */
        public static final String PA_RECONCILIATION_FILE_PATH = "PA_RECONCILIATION_FILE_PATH";
        /*
         * 指定交易日期
         */
        public static final String TRAN_DATE = "TRAN_DATE";
        /*
         * 开始日期
         */
        public static final String BEGIN_DATE = "BEGIN_DATE";
        /*
         * 对账文件是否OK
         */
        public static final String IS_OK = ".ok.enc";
        /*
         * 文件后缀名
         */
        public static final String ENC = ".enc";
        public static final String respCode = "respCode";
        public static final String ftpCode = "ftpCode";
        public static final String isReport = "isReport";

        /*
         * 回单网址
         */
        public static final String BANK_RECEIPT_URL = "BANK_RECEIPT_URL";
    }

    public static class FILE_BUSINESS_TYPE {
        /*
         * 充值
         */
        public static final String CZ = "1";
        /*
         * 提现
         */
        public static final String TX = "2";
        /*
         * 交易
         */
        public static final String JY = "3";
        /*
         * 转出
         */
        public static final String TURN_OUT = "4";
        /*
         * 转入
         */
        public static final String TURN_IN = "5";
        /*
         * 转移
         */
        public static final String TURN = "6";
    }
    public static class QUERY_TYPE{

        /*
         * 收入
         */
        public static final String INCOME = "1";
        /*
         * 支出
         */
        public static final String PAY = "2";
    }
}
