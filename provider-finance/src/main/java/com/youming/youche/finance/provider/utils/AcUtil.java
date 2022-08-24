package com.youming.youche.finance.provider.utils;


import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.finance.commons.util.CommonUtil;

/**
 * @author zengwen
 * @date 2022/4/14 17:05
 */
public class AcUtil {

    static final long  ACCOUNT_STATEMENT = 0L;
    /**
     * 创建招商车挂靠车账单编号 AS + 八位数字(从0开始)
     */
    public static String createAccountStatementNum(RedisUtil redisUtil) {

        String tempOrderId = (ACCOUNT_STATEMENT + redisUtil.incr(EnumConsts.RemoteCache.BILL_BUM)+"");

        Long orderId = Long.valueOf(tempOrderId);
        String order = "" ;
        if (orderId < 10000000L) {
            if (tempOrderId.length() == 1) {
                order = "AS0000000" + tempOrderId;
            } else if (tempOrderId.length() == 2) {
                order = "AS000000" + tempOrderId;
            } else if (tempOrderId.length() == 3) {
                order = "AS00000" + tempOrderId;
            } else if (tempOrderId.length() == 4) {
                order = "AS0000" + tempOrderId;
            } else if (tempOrderId.length() == 5) {
                order = "AS000" + tempOrderId;
            } else if (tempOrderId.length() == 6) {
                order = "AS00" + tempOrderId;
            } else if (tempOrderId.length() == 7) {
                order = "AS0" + tempOrderId;
            }
        } else {
            order = order + tempOrderId;
        }
        return order;
    }
}
