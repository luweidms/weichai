package com.youming.youche.finance.provider.utils;

import com.youming.youche.commons.exception.BusinessException;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/6/10 11:43
 */
public class MatchAmountUtil {

    public static final String get = "get";
    public static final String matchAmount = "setMatchAmount";
    public static final String matchIncome = "setMatchIncome";
    public static final String matchBackIncome = "setMatchBackIncome";

    public static List<? extends Object> matchAmount(long amount, long income, long backIncome, String fieldName,
                                                     List<? extends Object> list) {
        double incomeRatio = ((double) income) / ((double) amount);
        double backIncomeRatio = ((double) backIncome) / ((double) amount);
        Method method = null;
        Method matchAmountMethod = null;
        Method matchIncomeMethod = null;
        Method matchBackIncomeMethod = null;
        Long orderAmount = 0L;
        if (list == null || list.size() <= 0) {
            return list;
        }
        Object object = list.get(0);
        try {
            method = object.getClass().getDeclaredMethod(get + toFirstLeftUpperCase(fieldName));
            matchAmountMethod = object.getClass().getMethod(matchAmount, new Class[]{Long.class});
            matchIncomeMethod = object.getClass().getMethod(matchIncome, new Class[]{Long.class});
            matchBackIncomeMethod = object.getClass().getMethod(matchBackIncome, new Class[]{Long.class});
            for (Object obj : list) {
                if (method != null) {
                    orderAmount = (Long) method.invoke(obj);
                }
                if (orderAmount == null || orderAmount == 0L) {
                    continue;
                }
                if (amount > orderAmount) {
                    if (matchAmountMethod != null) {
                        matchAmountMethod.invoke(obj, new Object[]{orderAmount});
                    }
                    if (matchIncomeMethod != null) {
                        matchIncomeMethod.invoke(obj, new Object[]{new Double(orderAmount * incomeRatio).longValue()});
                    }
                    if (matchBackIncomeMethod != null) {
                        matchBackIncomeMethod.invoke(obj, new Object[]{new Double(orderAmount * backIncomeRatio).longValue()});
                    }
                    amount -= orderAmount;
                    income -= new Double(orderAmount * incomeRatio).longValue();
                    backIncome -= new Double(orderAmount * backIncomeRatio).longValue();
                } else if (amount <= orderAmount) {
                    if (matchAmountMethod != null) {
                        matchAmountMethod.invoke(obj, new Object[]{amount});
                    }
                    if (matchIncomeMethod != null) {
                        matchIncomeMethod.invoke(obj, new Object[]{income});
                    }
                    if (matchBackIncomeMethod != null) {
                        matchBackIncomeMethod.invoke(obj, new Object[]{backIncome});
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("数据处理异常");
        }
        return list;
    }

    /**
     * 字符串首字符转换成大写
     * @param str
     * @return
     */
    public static String toFirstLeftUpperCase(String str) {
        if (StringUtils.isEmpty(str) || str.length() < 2) {
            return str;
        }
        String firstLeft = str.substring(0, 1).toUpperCase();
        return firstLeft + str.substring(1, str.length());
    }
}
