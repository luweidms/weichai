package com.youming.youche.market.commons;

public class RepairConsts {
        /**
         * 司机是否确认  1为确认 0为驳回
         */
        public static class IS_SURE{
            public static int YES = 1;
            public static int NO = 0;
        }

        /**
         * 服务商维修状态
         */
        public static class REPAIR_STATE{
            /**待司机确认*/
            public static int WAIT_DRIVER = 1;
            /**司机驳回*/
            public static int DRIVER_FAIL = 2;
            /**待审核*/
            public static int WAIT_AUDIT = 3;
            /**审核中*/
            public static int AUDITING = 4;
            /**审核不通过*/
            public static int AUDIT_FAIL = 5;
            /**未到期**/
            public static int UNEXPIRED = 6;
            /***已到期*/
            public static int BE_EXPIRED = 7;
            /**收款待确认*/
            public static int RECEIPT_TO_BE_CONFIRMED = 8;
            /**已收款*/
            public static int PAID_FOR =9;
        }

        /**
         * app维修状态
         */
        public static class APP_REPAIR_STATE{
            /**待司机确认*/
            public static int WAIT_DRIVER = 1;
            /**司机驳回*/
            public static int DRIVER_FAIL = 2;
            /**待审核*/
            public static int WAIT_AUDIT = 3;
            /**审核中*/
            public static int AUDITING = 4;
            /**审核通过*/
            public static int PASS_AUDIT = 5;
            /**审核不通过*/
            public static int AUDIT_FAIL = 6;
            /**现金支付中*/
            public static int CASH_PAYMENT= 7;
            /**已支付*/
            public static int PAID =8;
        }
}
