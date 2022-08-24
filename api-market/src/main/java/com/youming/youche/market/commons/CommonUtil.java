package com.youming.youche.market.commons;

import com.youming.youche.util.HtmlEncoder;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class CommonUtil {

        private static final DecimalFormat nf = new DecimalFormat("#####0.00");
        private static final String NULL_STRING = "";
        private static final Log log = LogFactory.getLog(CommonUtil.class);
        private static Class[] ac;
        private static HashSet propertyTypesForCopy;

        public CommonUtil() {
        }

        public static void exchangeProperties(Object newObj, Object oldObj, String[] filedList) {
            for(int i = 0; i < filedList.length; ++i) {
                try {
                    Object newValue = PropertyUtils.getSimpleProperty(newObj, filedList[i]);
                    PropertyUtils.setSimpleProperty(newObj, filedList[i], PropertyUtils.getSimpleProperty(oldObj, filedList[i]));
                    PropertyUtils.setSimpleProperty(oldObj, filedList[i], newValue);
                } catch (Exception var5) {
                    var5.printStackTrace();
                }
            }

        }

        public static String formatDate(Date date, String format) {
            if (date == null) {
                return "";
            } else {
                if (format.indexOf("h") > 0) {
                    format = format.replace('h', 'H');
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                return simpleDateFormat.format(date);
            }
        }

        public static String formatDate(Date date) {
            return formatDate(date, "yyyy-MM-dd");
        }

        public static Date parseDate(String str, String format) {
            try {
                if (str != null && !str.equals("")) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                    return simpleDateFormat.parse(str);
                } else {
                    return null;
                }
            } catch (Exception var3) {
                return new Date();
            }
        }

        public static Date parseDate(String str) {
            if (str != null && str.trim().length() != 0) {
                if (str.length() == 10) {
                    return parseDate(str, "yyyy-MM-dd");
                } else if (str.length() == 13) {
                    return parseDate(str, "yyyy-MM-dd HH");
                } else if (str.length() == 16) {
                    return parseDate(str, "yyyy-MM-dd HH:mm");
                } else if (str.length() == 19) {
                    return parseDate(str, "yyyy-MM-dd HH:mm:ss");
                } else {
                    return str.length() >= 21 ? parseDate(str, "yyyy-MM-dd HH:mm:ss.S") : null;
                }
            } else {
                return null;
            }
        }

//        /** @deprecated */
//        public static void addBetweenDate(Criteria criteria, String startField, String endField, String startParam, String endParam) {
//            HttpServletRequest request = SysContexts.getRequest();
//            Date startDate = null;
//            Date endDate = null;
//            String s = request.getParameter(startParam);
//            if (StringUtils.isNotEmpty(s)) {
//                startDate = parseDate(s);
//            }
//
//            s = request.getParameter(endParam);
//            if (StringUtils.isNotEmpty(s)) {
//                endDate = parseDate(s);
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTime(endDate);
//                calendar.add(6, 1);
//                endDate = calendar.getTime();
//            }
//
//            if (startDate != null && endDate != null) {
//                criteria.add(Restrictions.and(Restrictions.ge(startField, startDate), Restrictions.lt(endField, endDate)));
//            } else if (startDate != null) {
//                criteria.add(Restrictions.ge(startField, startDate));
//            } else if (endDate != null) {
//                criteria.add(Restrictions.lt(endField, endDate));
//            }
//
//        }

        public static String killNull(Object o, String ds) {
            if (o instanceof String) {
                return killNull((String)o, ds);
            } else if (o instanceof Double) {
                return killNull((Double)o, ds);
            } else {
                return o != null ? o.toString() : ds;
            }
        }

        public static String killNull(Object o) {
            return killNull(o, "");
        }

        public static String killNull(Double o, String ds) {
            return o != null ? nf.format(o) : ds;
        }

        public static String killNull(Double o) {
            return killNull(o, "");
        }

        public static String killNull(double o) {
            return o != 0.0D ? nf.format(o) : "";
        }

        public static String killNull(BigDecimal o) {
            return killNull(o, "");
        }

        public static String killNull(BigDecimal o, String nullString) {
            if (o != null) {
                return nf.format(o.doubleValue());
            } else {
                return "".equals(nullString) ? "" : "0";
            }
        }

        public static String killNull(long o) {
            return String.valueOf(o);
        }

        public static String killNull(Long o) {
            return o == null ? "" : String.valueOf(o);
        }

        public static String killNull(String s, String ds, boolean isView, boolean isEncode) {
            if (!isEmpty(s)) {
                return isEncode ? HtmlEncoder.encode(s.trim(), isView) : s.trim();
            } else {
                return ds;
            }
        }

        public static String killNull(String inObj, boolean isView, boolean isEncode) {
            return killNull(inObj, "", isView, isEncode);
        }

        public static String killNull(String inObj, String toStr) {
            return killNull(inObj, toStr, true, false);
        }

        public static String killNull(String inObj, boolean isView) {
            return killNull(inObj, isView, false);
        }

        public static String killNull(String inObj) {
            return killNull(inObj, true);
        }

        public static boolean isNumber(String str) {
            try {
                if (isEmpty(str)) {
                    return false;
                } else {
                    Double ret = new Double(str);
                    log.debug(ret);
                    return true;
                }
            } catch (Exception var2) {
                return false;
            }
        }

        public static String toStr(String s, int len) {
            if (s == null) {
                s = "";
            } else {
                s = s.trim();
            }

            return StringUtils.repeat("0", len - s.length()) + s;
        }

        public static String toStr(long n, int len) {
            String s = Long.toString(n);
            return StringUtils.repeat("0", len - s.length()) + s;
        }

        public static String leftPad(String s, int n, String regex) {
            return StringUtils.leftPad(s, s.length() + n, regex);
        }

        public static String leftPad(String s, int n, char regex) {
            return StringUtils.leftPad(s, s.length() + n, regex);
        }

        public static String rightPad(String s, int n, String regex) {
            return StringUtils.rightPad(s, s.length() + n, regex);
        }

        public static String rightPad(String s, int n, char regex) {
            return StringUtils.rightPad(s, s.length() + n, regex);
        }

        public static String fieldName2varName(String columnName) {
            return fieldName2varName(columnName, "_");
        }

        public static String fieldName2varName(String columnName, String regex) {
            StringBuffer controlName = null;
            String temp = null;
            if (columnName.indexOf(regex) < 0) {
                return columnName;
            } else {
                StringTokenizer st = new StringTokenizer(columnName, regex);
                if (st.hasMoreTokens()) {
                    controlName = new StringBuffer(st.nextToken().toLowerCase());
                }

                while(st.hasMoreTokens()) {
                    temp = st.nextToken().toLowerCase();
                    controlName.append(temp.substring(0, 1).toUpperCase()).append(temp.substring(1, temp.length()));
                }

                return controlName == null ? "" : controlName.toString();
            }
        }

        public static String varName2fieldName(String varName) {
            return varName2fieldName(varName, "_");
        }

        public static String varName2fieldName(String varName, String regex) {
            char[] temp = varName.toCharArray();
            StringBuffer filedName = new StringBuffer();
            String[] index = new String[5];
            int i = 0;

            for(i = 0; i < temp.length; ++i) {
                if (Character.isUpperCase(temp[i])) {
                    index[i] = String.valueOf(temp[i]);
                    ++i;
                }
            }

            String[] strArray = varName.split("[A-Z]");

            for(i = 0; i < strArray.length; ++i) {
                if (i != strArray.length - 1) {
                    filedName.append(strArray[i]).append(regex).append(index[i].toLowerCase());
                } else {
                    filedName.append(strArray[i]);
                }
            }

            return filedName.toString();
        }

        public static String[] stringToStrArray(String input, String delimiter) {
            if (isEmpty(input)) {
                return null;
            } else {
                String[] cmd = input.split(delimiter);
                return cmd;
            }
        }

        public static List stringToStrList(String input, String delimiter) {
            return isEmpty(input) ? null : new ArrayList(Arrays.asList(stringToStrArray(input, delimiter)));
        }

        public static String strArrayToString(String[] needtoTrans, String delimiter) {
            return list2String(Arrays.asList(needtoTrans), delimiter);
        }

        public static String list2String(List list, String seperator) {
            if (list != null && list.size() != 0) {
                StringBuffer sb = new StringBuffer();

                for(int i = 0; i < list.size(); ++i) {
                    if (i != 0) {
                        sb.append(seperator);
                    }

                    sb.append(list.get(i));
                }

                return sb.toString();
            } else {
                return "";
            }
        }

        public static boolean isEmpty(Object emptyObect) {
            boolean result = true;
            if (emptyObect == null) {
                return true;
            } else {
                if (emptyObect instanceof String) {
                    result = emptyObect.toString().trim().length() == 0 || emptyObect.toString().trim().equals("null");
                } else if (emptyObect instanceof Collection) {
                    result = ((Collection)emptyObect).size() == 0;
                } else {
                    result = emptyObect == null || emptyObect.toString().trim().length() < 1;
                }

                return result;
            }
        }

        public static boolean isNotEmpty(String str) {
            return !isEmpty(str);
        }

        public static String getStringParamValue(HttpServletRequest request, String key, String defaultValue) {
            Object value = request.getParameter(key);
            if (value == null) {
                value = request.getAttribute(key);
            }

            return value != null ? value.toString() : defaultValue;
        }

        public static String getStringParamValue(HttpServletRequest request, String key) {
            return getStringParamValue(request, key, (String)null);
        }

        public static StringBuffer setVar(StringBuffer temp, String name, long value) {
            return setVar(temp, name, String.valueOf(value));
        }

        public static StringBuffer setVar(StringBuffer temp, String name, String value) {
            return setVar(temp, name, (Object)value);
        }

        public static StringBuffer setVar(StringBuffer temp, String name, Object value) {
            if (temp != null && name != null) {
                if (value == null || value.toString().length() == 0) {
                    value = "";
                }

                int start = temp.indexOf(name);
                return start < 0 ? temp : temp.replace(start, start + name.length(), value.toString());
            } else {
                return temp;
            }
        }

        public static Long[] stringToLong(String[] args) {
            if (args == null) {
                return null;
            } else {
                Long[] longs = new Long[args.length];
                int i = 0;

                for(int n = args.length; i < n; ++i) {
                    longs[i] = Long.valueOf(args[i]);
                }

                return longs;
            }
        }

        public static long[] stringTolong(String[] args) {
            if (args == null) {
                return null;
            } else {
                long[] longs = new long[args.length];
                int i = 0;

                for(int n = args.length; i < n; ++i) {
                    longs[i] = Long.valueOf(args[i]);
                }

                return longs;
            }
        }

        public static double[] stringTodouble(String[] args) {
            if (args == null) {
                return null;
            } else {
                double[] doubles = new double[args.length];
                int i = 0;

                for(int n = args.length; i < n; ++i) {
                    doubles[i] = Double.valueOf(args[i]);
                }

                return doubles;
            }
        }

        public static String getProperty(Object obj, String name) {
            try {
                return BeanUtils.getProperty(obj, name);
            } catch (Exception var3) {
                return "";
            }
        }

//        public static Object getObjectById(Class clazz, Long id) {
//            Session session = SysContexts.getEntityManager();
//            return session.get(clazz, id);
//        }
//
//        public static void updateObject(Object obj) {
//            Session session = SysContexts.getEntityManager();
//            session.saveOrUpdate(obj);
//        }
//
//        public static Criteria assembleEqCriteria(Criteria criteria, String[] args) {
//            return assembleCriteria(criteria, args, "eq");
//        }
//
//        public static Criteria assembleLikeCriteria(Criteria criteria, String[] args) {
//            return assembleCriteria(criteria, args, "like");
//        }
//
//        public static String getUID() {
//            return HtmlUtil.getUid();
//        }
//
//        public static boolean isAjaxRequest() {
//            if (SysContexts.getRequest().getAttribute("isAjaxRequest") == null) {
//                return false;
//            } else {
//                Boolean isAjaxRequest = (Boolean)SysContexts.getRequest().getAttribute("isAjaxRequest");
//                return isAjaxRequest;
//            }
//        }
//
//        private static Criteria assembleCriteria(Criteria criteria, String[] args, String flag) {
//            if (args != null && args.length != 0) {
//                int i = 0;
//
//                for(int n = args.length; i < n; ++i) {
//                    if (!isEmpty(SysContexts.getRequestParam(args[i]))) {
//                        String property = null;
//                        String label = null;
//                        if (args[i].indexOf(".") > 0) {
//                            int fp = args[i].indexOf(".");
//                            int sp = args[i].indexOf(".", fp);
//                            String entity = args[i].substring(0, fp);
//                            String alias = args[i].substring(fp + 1, sp);
//                            property = args[i].substring(sp + 1);
//                            label = alias + "." + property;
//                            criteria = criteria.createAlias(entity, alias);
//                        } else {
//                            property = args[i];
//                            label = args[i];
//                        }
//
//                        if (flag.equals("eq")) {
//                            criteria = criteria.add(Restrictions.eq(label, SysContexts.getRequestParam(property)));
//                        } else {
//                            if (!flag.equals("like")) {
//                                throw new BusinessException("不支持的查询类型");
//                            }
//
//                            criteria = criteria.add(Restrictions.ilike(label, SBC_to_DBC(SysContexts.getRequestParam(property).trim()), MatchMode.ANYWHERE));
//                        }
//                    }
//                }
//
//                return criteria;
//            } else {
//                return criteria;
//            }
//        }

        public static String getNowDate() {
            Calendar now = Calendar.getInstance();
            return getDateStr(now);
        }

        public static String getNowFullDate() {
            Calendar now = Calendar.getInstance();
            return getDateStr(now) + " " + getHour(now) + ":" + getMinute(now) + ":" + getSecond(now);
        }

        public static String getDateStr(Calendar cal) {
            return getYear(cal) + "-" + getMonth(cal) + "-" + getDay(cal);
        }

        public static String getYear(Calendar cal) {
            return String.valueOf(cal.get(1));
        }

        public static String getMonth(Calendar cal) {
            return strLen(String.valueOf(cal.get(2) + 1), 2);
        }

        public static String getDay(Calendar cal) {
            return strLen(String.valueOf(cal.get(5)), 2);
        }

        public static String getHour(Calendar cal) {
            return strLen(String.valueOf(cal.get(11)), 2);
        }

        public static String getMinute(Calendar cal) {
            return strLen(String.valueOf(cal.get(12)), 2);
        }

        public static String getSecond(Calendar cal) {
            return strLen(String.valueOf(cal.get(13)), 2);
        }

        public static String strLen(String s, int len) {
            if (s == null) {
                s = "";
            } else {
                s = s.trim();
            }

            int strLen = s.length();

            for(int i = 0; i < len - strLen; ++i) {
                s = "0" + s;
            }

            return s;
        }

        public static Timestamp convertStrToTimestamp(String timeStampStr) {
            Timestamp returnT = null;
            if (timeStampStr != null && !timeStampStr.trim().equals("")) {
                returnT = new Timestamp(parseDate(timeStampStr).getTime());
            }

            return returnT;
        }

        public static String getModifiedValue(Object newValue, Object oldValue, boolean isNull) {
            String str1 = newValue == null ? " " : killNull(newValue);
            String str2 = oldValue == null ? " " : killNull(oldValue);
            if (isNull) {
                return str1;
            } else {
                return !str1.equals(str2) ? str1 + "&nbsp;<font color='red'>[" + str2 + "]</font>" : str1;
            }
        }

        public static Throwable getRootCause(Throwable exception) {
            return exception.getCause() != null ? getRootCause(exception.getCause()) : exception;
        }

        public static String killZero(long id) {
            return id == 0L ? "" : String.valueOf(id);
        }

        public static final String SBC_to_DBC(String SBCstr) {
            if (isEmpty(SBCstr)) {
                return "";
            } else {
                SBCstr = SBCstr.trim();
                StringBuffer outStr = new StringBuffer();
                String str = "";
                byte[] b = null;
                StringBuffer sb = new StringBuffer(SBCstr);

                for(int i = 0; i < SBCstr.length(); ++i) {
                    try {
                        str = sb.substring(i, i + 1);
                        b = str.getBytes("unicode");
                    } catch (UnsupportedEncodingException var8) {
                        var8.printStackTrace();
                    }

                    if (b[3] == -1) {
                        b[2] = (byte)(b[2] + 32);
                        b[3] = 0;

                        try {
                            outStr.append(new String(b, "unicode"));
                        } catch (UnsupportedEncodingException var7) {
                            var7.printStackTrace();
                        }
                    } else {
                        outStr.append(str);
                    }
                }

                return outStr.toString();
            }
        }

//        public static String getCodeNameValue(String str) {
//            StringBuffer test = new StringBuffer();
//            String[] values = str != null ? str.split(",") : new String[0];
//
//            for(int i = 0; i < values.length; ++i) {
//                test.append(BusinessUtil.getCodeName(values[i])).append(" ");
//            }
//
//            return test.toString();
//        }

        static {
            ac = new Class[]{Long.class, Long.TYPE, Boolean.class, Boolean.TYPE, Integer.class, Integer.TYPE, Byte.class, Byte.TYPE, String.class, Date.class, java.sql.Date.class, Timestamp.class, BigDecimal.class, Float.class, Float.TYPE};
            propertyTypesForCopy = new HashSet(Arrays.asList(ac));
        }
    /**
     * 金额分转元 并保留几位小数 的Double类型数据
     */
    public static Double getDoubleFormatLongMoney(Long balance, int bl) {
        if (balance == null) {
            return null;
        }
        if (balance.longValue() == 0) {
            return 0.0;
        }
        Double money = ((double) balance) / 100;
        BigDecimal bg = new BigDecimal(money);
        double re = bg.setScale(bl, BigDecimal.ROUND_HALF_UP).doubleValue();
        return re;
    }
}
