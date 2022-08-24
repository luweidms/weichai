package com.youming.youche.order.provider.utils;

import com.youming.youche.order.domain.order.OilSourceRecord;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
@Component
public class MatchAmountUtil {

    public static final String get = "get";
    public static final String matchAmount = "setMatchAmount";
    public static final String matchNoPayOilAmount = "setMatchNoPayOil";
    public static final String matchNoRebateOilAmount = "setMatchNoRebateOil";
    public static final String matchNoCreditOilAmount = "setMatchNoCreditOil";
    public static final String matchIncome = "setMatchIncome";
    public static final String matchBackIncome = "setMatchBackIncome";
    /**
     * 订单金额匹配
     *
     * @param amount     匹配金额
     * @param income     匹配利润
     * @param backIncome 匹配返现利润
     * @param fieldName  要匹配的字段属性(比如字段属性noPayCash，传过来为noPayCash)
     * @param list       集合
     * @return
     * @throws Exception
     */
    public  List<OilSourceRecord> matchAmountOilSourceRecord(long amount, long income, long backIncome,
                                                     List<OilSourceRecord> list) {
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
        for (OilSourceRecord oilSourceRecord : list) {
            if (oilSourceRecord.getNoPayBalance() != null) {
                orderAmount = oilSourceRecord.getNoPayBalance();
            }
            if (orderAmount == null || orderAmount == 0L) {
                continue;
            }
            if (amount > orderAmount) {
                oilSourceRecord.setMatchAmount(orderAmount);
//                oilSourceRecord.setMatchIncome()
//                oilSourceRecord.setMatchBackIncome()
                amount -= orderAmount;
                income -= new Double(orderAmount * incomeRatio).longValue();
                backIncome -= new Double(orderAmount * backIncomeRatio).longValue();
            }else if (amount <= orderAmount){
                oilSourceRecord.setMatchAmount(amount);
                break;
            }
        }
        return list;
    }

    public static List<? extends Object> matchAmount(long amount, long income, long backIncome, String fieldName,
                                                     List<? extends Object> list) throws Exception {
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
                    matchAmountMethod.invoke(obj,new Object[]{orderAmount});
                }
                if (matchIncomeMethod != null) {
                    matchIncomeMethod.invoke(obj,new Object[]{new Double(orderAmount * incomeRatio).longValue()});
                }
                if (matchBackIncomeMethod != null) {
                    matchBackIncomeMethod.invoke(obj,new Object[]{new Double(orderAmount * backIncomeRatio).longValue()});
                }
                amount -= orderAmount;
                income -= new Double(orderAmount * incomeRatio).longValue();
                backIncome -= new Double(orderAmount * backIncomeRatio).longValue();
            } else if (amount <= orderAmount) {
                if (matchAmountMethod != null) {
                    matchAmountMethod.invoke(obj,new Object[]{amount});
                }
                if (matchIncomeMethod != null) {
                    matchIncomeMethod.invoke(obj,new Object[]{income});
                }
                if (matchBackIncomeMethod != null) {
                    matchBackIncomeMethod.invoke(obj,new Object[]{backIncome});
                }
                break;
            }
        }
        return list;
    }

    /**
     * 订单金额匹配
     * @param amount 匹配金额
     * @param income 匹配利润
     * @param backIncome 匹配返现利润
     * @param fieldName 要匹配的字段属性(比如字段属性noPayCash，传过来为noPayCash)
     * @param list 集合
     * @return
     * @throws Exception
     */
    public static List<? extends Object> matchAmounts(long amount, long income, long backIncome, String noPayOil,String noRebateOil,String noCreditOil,
                                                      List<? extends Object> list){
        Method getNoPayOil = null;
        Method getNoRebateOil = null;
        Method getNoCreditOil = null;
        Method matchAmountMethod = null;
        Method matchNoPayOilMethod = null;
        Method matchNoRebateOilMethod = null;
        Method matchNoCreditOilMethod = null;

        Long noPayOilAmount = 0L;
        Long noRebateOilAmount = 0L;
        Long noCreditOilAmount = 0L;
        if (list == null || list.size() <= 0) {
            return list;
        }
        Object object = list.get(0);
        try {
            getNoPayOil = object.getClass().getDeclaredMethod(get + toFirstLeftUpperCase(noPayOil));
            getNoRebateOil = object.getClass().getDeclaredMethod(get + toFirstLeftUpperCase(noRebateOil));
            getNoCreditOil = object.getClass().getDeclaredMethod(get + toFirstLeftUpperCase(noCreditOil));
            matchAmountMethod = object.getClass().getMethod(matchAmount, new Class[]{Long.class});
            matchNoPayOilMethod = object.getClass().getMethod(matchNoPayOilAmount, new Class[]{Long.class});
            matchNoRebateOilMethod = object.getClass().getMethod(matchNoRebateOilAmount, new Class[]{Long.class});
            matchNoCreditOilMethod = object.getClass().getMethod(matchNoCreditOilAmount, new Class[]{Long.class});
            for (Object obj : list) {
                if (getNoPayOil != null) {
                    noPayOilAmount = (Long) getNoPayOil.invoke(obj);
                }
                if (getNoRebateOil != null) {
                    noRebateOilAmount = (Long) getNoRebateOil.invoke(obj);
                }
                if (getNoCreditOil != null) {
                    noCreditOilAmount = (Long) getNoCreditOil.invoke(obj);
                }
                if ((noPayOilAmount == null || noPayOilAmount == 0L) && (noRebateOilAmount == null || noRebateOilAmount == 0L) && (noCreditOilAmount == null || noCreditOilAmount == 0L) ) {
                    continue;
                }
                Long totalAmount = ((noPayOilAmount == null ? 0L : noPayOilAmount) + (noRebateOilAmount == null ? 0L : noRebateOilAmount) + (noCreditOilAmount == null ? 0L : noCreditOilAmount));
                if (amount > totalAmount) {
                    if (matchAmountMethod != null) {
                        matchAmountMethod.invoke(obj,new Object[]{totalAmount});
                    }
                    if (matchNoPayOilMethod != null) {
                        if (noPayOilAmount != null) {
                            matchNoPayOilMethod.invoke(obj,new Object[]{noPayOilAmount});
                        }
                    }
                    if (matchNoRebateOilMethod != null) {
                        if (noRebateOilAmount != null) {
                            matchNoRebateOilMethod.invoke(obj,new Object[]{noRebateOilAmount});
                        }
                    }
                    if (matchNoCreditOilMethod != null) {
                        if (noCreditOilAmount != null) {
                            matchNoCreditOilMethod.invoke(obj,new Object[]{noCreditOilAmount});
                        }
                    }
                    amount -= totalAmount;
                } else if (amount <= totalAmount) {
                    if (matchAmountMethod != null) {
                        matchAmountMethod.invoke(obj,new Object[]{amount});
                    }
                    if (matchNoPayOilMethod != null && amount > 0) {
                        if (noPayOilAmount != null) {
                            if (amount > noPayOilAmount) {
                                matchNoPayOilMethod.invoke(obj,new Object[]{noPayOilAmount});
                                amount -= noPayOilAmount;
                            } else {
                                matchNoPayOilMethod.invoke(obj,new Object[]{amount});
                                amount = 0;
                            }
                        } else {
                            if (matchNoRebateOilMethod != null && amount > 0) {
                                if (noRebateOilAmount != null) {
                                    if (amount > noRebateOilAmount) {
                                        matchNoRebateOilMethod.invoke(obj,new Object[]{noRebateOilAmount});
                                        amount -= noRebateOilAmount;
                                    } else {
                                        matchNoRebateOilMethod.invoke(obj,new Object[]{amount});
                                        amount = 0;
                                    }
                                } else {
                                    if (matchNoCreditOilMethod != null && amount > 0) {
                                        if (noCreditOilAmount != null) {
                                            if (amount > noCreditOilAmount) {
                                                matchNoCreditOilMethod.invoke(obj,new Object[]{noCreditOilAmount});
                                                amount -= noCreditOilAmount;
                                            } else {
                                                matchNoCreditOilMethod.invoke(obj,new Object[]{amount});
                                                amount = 0;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (matchNoRebateOilMethod != null && amount > 0) {
                        if (noRebateOilAmount != null) {
                            if (amount > noRebateOilAmount) {
                                matchNoRebateOilMethod.invoke(obj,new Object[]{noRebateOilAmount});
                                amount -= noRebateOilAmount;
                            } else {
                                matchNoRebateOilMethod.invoke(obj,new Object[]{amount});
                                amount = 0;
                            }
                        } else {
                            if (matchNoCreditOilMethod != null && amount > 0) {
                                if (noCreditOilAmount != null) {
                                    if (amount > noCreditOilAmount) {
                                        matchNoCreditOilMethod.invoke(obj,new Object[]{noCreditOilAmount});
                                        amount -= noCreditOilAmount;
                                    } else {
                                        matchNoCreditOilMethod.invoke(obj,new Object[]{amount});
                                        amount = 0;
                                    }
                                }
                            }
                        }
                    }
                    if (matchNoCreditOilMethod != null && amount > 0) {
                        if (noCreditOilAmount != null) {
                            if (amount > noCreditOilAmount) {
                                matchNoCreditOilMethod.invoke(obj,new Object[]{noCreditOilAmount});
                                amount -= noCreditOilAmount;
                            } else {
                                matchNoCreditOilMethod.invoke(obj,new Object[]{amount});
                                amount = 0;
                            }
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
