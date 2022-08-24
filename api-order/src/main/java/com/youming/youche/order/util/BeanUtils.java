//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.youming.youche.order.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class BeanUtils extends org.apache.commons.beanutils.BeanUtils {
    private static Log log = LogFactory.getLog(BeanUtils.class);

    public BeanUtils() {
    }

    public static void populate(Object bean, Map properties) throws IllegalAccessException, InvocationTargetException {
        populate(bean, properties, "");
    }

    public static void populate(Object bean, Map properties, String prefix) throws IllegalAccessException, InvocationTargetException {
        populate(bean, properties, prefix, "UTF-8");
    }

    public static void populate(Object bean, Map properties, String prefix, String encode) throws IllegalAccessException, InvocationTargetException {
        if (bean != null && properties != null) {
            if (prefix == null) {
                prefix = "";
            }

            if (log.isDebugEnabled()) {
                log.debug("BeanUtils.populate(" + bean + ", " + properties + ")");
            }

            Iterator itr = properties.entrySet().iterator();

            while(itr.hasNext()) {
                Entry<String, Object> entry = (Entry)itr.next();
                String name = (String)entry.getKey();
                if (name != null) {
                    Object value = enCoding(entry.getValue(), encode);
                    name = name.replaceFirst(prefix, "");
                    org.apache.commons.beanutils.BeanUtils.setProperty(bean, name, value);
                }
            }

        }
    }

    public static Object enCoding(Object value, String encoding) {
        try {
            if (value == null) {
                return null;
            } else {
                Object converValue = null;
                if (value instanceof String) {
                    converValue = enCoding((String)value, encoding).trim();
                } else if (value instanceof String[]) {
                    String[] vals = (String[])((String[])value);
                    if (((String[])((String[])value)).length > 0) {
                        converValue = new String[vals.length];

                        for(int i = 0; i < vals.length; ++i) {
                            if (StringUtils.isNotEmpty(vals[i])) {
                                vals[i] = enCoding(vals[i], encoding);
                                ((String[])((String[])converValue))[i] = vals[i].trim();
                            }
                        }
                    }
                } else {
                    converValue = String.valueOf(value);
                }

                if (converValue instanceof String[] && ((String[])((String[])converValue)).length > 0) {
                    converValue = StringUtils.join((String[])((String[])converValue), ",");
                }

                return converValue;
            }
        } catch (Exception var5) {
            log.error("对象转码异常:" + var5);
            return value;
        }
    }

    public static String enCoding(String value) {
        return enCoding(value, "UTF-8");
    }

    public static String enCoding(String value, String encoding) {
        try {
            if (StringUtils.isNotEmpty(value)) {
                String realEnCode = getEncoding(value);
                if ("ISO8859-1".equalsIgnoreCase(realEnCode)) {
                    value = new String(value.getBytes(realEnCode), encoding);
                    return value;
                } else if (!"GBK".equalsIgnoreCase(realEnCode) && !encoding.equalsIgnoreCase(realEnCode)) {
                    value = new String(value.getBytes(realEnCode), encoding);
                    return value;
                } else {
                    return value;
                }
            } else {
                return value;
            }
        } catch (Exception var3) {
            log.error("字符串转码异常:" + var3);
            return value;
        }
    }

    public static String getEncoding(String str) {
        String[] encodes = new String[]{"ISO8859-1", "GBK", "GB2312", "BIG5", "UTF-8", "UTF-16", "EUC-KR", "Shift_JIS", "EUC-JP", "ASCII"};

        for(int i = 0; i < encodes.length; ++i) {
            String encode = encodes[i];

            try {
                if (Charset.forName(encode).newEncoder().canEncode(str)) {
                    return encode;
                }
            } catch (Exception var5) {
                log.error("判断字符串编码异常:" + var5);
            }
        }

        return "";
    }

    public static final Map<String, Object> convertBean2Map(Object bean){
        return convertBean2Map(bean, (String)null);
    }

    public static final Map<String, Object> convertBean2Map(Object bean, String prefix)  {
        if (prefix == null) {
            prefix = "";
        }

        if (bean instanceof Map) {
            return (Map)bean;
        } else {
            Map<String, Object> returnMap = new HashMap();
            BeanInfo beanInfo = null;
            try {
                beanInfo = Introspector.getBeanInfo(bean.getClass());
            } catch (IntrospectionException e) {
                e.printStackTrace();
            }
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for(int i = 0; i < propertyDescriptors.length; ++i) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                String propertyName = descriptor.getName();
                if (!propertyName.equals("class")) {
                    Method readMethod = descriptor.getReadMethod();
                    Object result = null;
                    try {
                        result = readMethod.invoke(bean);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    if (result != null) {
                        returnMap.put(prefix + propertyName, result);
                    } else {
                        returnMap.put(prefix + propertyName, "");
                    }
                }
            }

            return returnMap;
        }
    }
}
