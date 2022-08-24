package com.youming.youche.order.commons;

/**
 * 异常监控常量
 */
public class MonitorEventsConst {

    //时效事件
    public static class EFFICIENCY_EVENTS {
        //晚靠台
        public static final String DEPEND_LATE = "101";
        //异常停留
        public static final String STOP = "102";
        //堵车缓行
        public static final String SLOW = "103";
        //线路偏移
        public static final String DEVIATION = "104";
        //预计迟到
        public static final String PREDICATEDELAY = "105";
        //迟到
        public static final String LATE = "106";
        //设备离线
        public static final String OFFLINE = "107";
        //全部时效
        public static final String ALL = "100";

        public static final String getNameByCode(String code) {
            if (DEPEND_LATE.equals(code)) {
                return "晚靠台";
            } else if (STOP.equals(code)) {
                return "异常停留";
            } else if (SLOW.equals(code)) {
                return "堵车缓行";
            } else if (DEVIATION.equals(code)) {
                return "线路偏移";
            } else if (PREDICATEDELAY.equals(code)) {
                return "预计迟到";
            } else if (LATE.equals(code)) {
                return "迟到";
            } else if (OFFLINE.equals(code)) {
                return "设备离线";
            } else {
                return "时效";
            }
        }
    }

    //盘点项
    public static class CHECK_EVENTS {
        //盘点
        public static final String CHECK = "200";
        //24h+
        public static final String RETENTION24 = "201";
        //48h+
        public static final String RETENTION48 = "202";
        //72h+
        public static final String RETENTION72 = "203";
    }

    public static class EXPIRE_EVENTS {
        //行驶证审
        public static final String VEHICLEVALIDITY = "301";
        //驾驶证审
        public static final String OPRATEVALIDITY = "302";
        //交强险
        public static final String INSURANCE = "303";
        //保养
        public static final String MAINTAIN = "304";
        //商业险
        public static final String BUSI_INSURANCE = "305";
        //其他险
        public static final String OTHER_INSURANCE = "306";
    }

    public static class OTHER_EVENTS {
        //找回货
        public static final String BACK_GOODS = "400";
    }
}
